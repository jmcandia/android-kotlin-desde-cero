package com.ejemplo.miscontactos.data

import com.ejemplo.miscontactos.data.local.ContactoDao
import com.ejemplo.miscontactos.data.local.FavoritoDao
import com.ejemplo.miscontactos.data.local.FavoritoEntity
import com.ejemplo.miscontactos.data.local.aDominio
import com.ejemplo.miscontactos.data.local.aEntity
import com.ejemplo.miscontactos.data.remote.ContactApi
import com.ejemplo.miscontactos.data.remote.aDominio
import com.ejemplo.miscontactos.data.remote.aErrorDatos
import com.ejemplo.miscontactos.data.remote.aRequest
import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.DatosContacto
import com.ejemplo.miscontactos.model.ErrorDatos
import com.ejemplo.miscontactos.model.PaginaContactos
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

@Singleton
class ContactosRepositoryImpl @Inject constructor(
    private val api: ContactApi,
    private val contactoDao: ContactoDao,
    private val favoritoDao: FavoritoDao,
    private val json: Json
) : ContactosRepository {

    private val _cambios = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val cambios: Flow<Unit> = _cambios.asSharedFlow()

    override val favoritos: Flow<Set<Int>> =
        favoritoDao.observarIds().map { ids -> ids.toSet() }

    override suspend fun obtenerPagina(pagina: Int, busqueda: String?): Result<PaginaContactos> {
        val esPrimeraPaginaSinFiltro = pagina == 0 && busqueda.isNullOrBlank()

        val resultado = llamarApi {
            api.obtenerContactos(pagina = pagina, tamano = TAMANO_PAGINA, busqueda = busqueda).aDominio()
        }

        resultado.onSuccess { paginaContactos ->
            if (esPrimeraPaginaSinFiltro) {
                contactoDao.reemplazarTodos(
                    paginaContactos.contactos.mapIndexed { indice, contacto -> contacto.aEntity(indice) }
                )
            }
        }

        // Sin conexión: si pedíamos la primera página sin filtro, usamos la copia local.
        val error = resultado.exceptionOrNull()
        if (error is ErrorDatos.SinConexion && esPrimeraPaginaSinFiltro) {
            val guardados = contactoDao.obtenerTodos()
            if (guardados.isNotEmpty()) {
                return Result.success(
                    PaginaContactos(
                        contactos = guardados.map { it.aDominio() },
                        pagina = 0,
                        totalPaginas = 1,
                        desdeCache = true
                    )
                )
            }
        }
        return resultado
    }

    override suspend fun obtenerContacto(id: Int): Result<Contacto> {
        val resultado = llamarApi { api.obtenerContacto(id).aDominio() }
        if (resultado.exceptionOrNull() is ErrorDatos.SinConexion) {
            contactoDao.obtenerPorId(id)?.let { return Result.success(it.aDominio()) }
        }
        return resultado
    }

    override suspend fun crear(datos: DatosContacto): Result<Contacto> =
        llamarApi { api.crearContacto(datos.aRequest()).aDominio() }
            .onSuccess { _cambios.tryEmit(Unit) }

    override suspend fun actualizar(id: Int, datos: DatosContacto): Result<Contacto> =
        llamarApi { api.actualizarContacto(id, datos.aRequest()).aDominio() }
            .onSuccess { _cambios.tryEmit(Unit) }

    override suspend fun eliminar(id: Int): Result<Unit> =
        llamarApi { api.eliminarContacto(id) }
            .onSuccess {
                contactoDao.borrar(id)
                favoritoDao.borrar(id)
                _cambios.tryEmit(Unit)
            }

    override suspend fun alternarFavorito(id: Int) {
        if (favoritoDao.esFavorito(id)) {
            favoritoDao.borrar(id)
        } else {
            favoritoDao.insertar(FavoritoEntity(contactoId = id))
        }
    }

    /**
     * Ejecuta una llamada a la API y convierte cualquier fallo en un [ErrorDatos].
     * La cancelación se vuelve a lanzar: no es un error, es la coroutine que termina.
     */
    private suspend fun <T> llamarApi(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.aErrorDatos(json))
        }

    companion object {
        const val TAMANO_PAGINA = 20
    }
}
