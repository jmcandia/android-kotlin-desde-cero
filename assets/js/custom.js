// La forma estándar y recomendada por MkDocs Material para Instant Loading
if (typeof document$ !== "undefined") {
  document$.subscribe(function() {
    setupAccordionNav();
  });
} else {
  // Respaldo por si se carga antes de inicializar la variable global
  document.addEventListener("DOMContentLoaded", setupAccordionNav);
}

function setupAccordionNav() {
  const nav = document.querySelector(".md-nav--primary");
  if (!nav) return;

  // 1. Maneja los clics manuales del usuario en los menús desplegables
  nav.addEventListener("click", function(event) {
    const label = event.target.closest(".md-nav__link");
    if (!label) return;

    const toggleId = label.getAttribute("for");
    if (!toggleId) return;

    const toggle = document.getElementById(toggleId);
    // Solo actuamos si el menú se va a abrir (si pasa a true)
    if (toggle && !toggle.checked) {
      const parentLi = toggle.closest("li.md-nav__item--nested");
      if (parentLi) {
        // Obtenemos los menús hermanos del mismo nivel jerárquico
        const siblingLis = parentLi.parentElement.children;
        for (let sibling of siblingLis) {
          if (sibling !== parentLi) {
            const siblingToggle = sibling.querySelector(".md-nav__toggle");
            if (siblingToggle) {
              siblingToggle.checked = false; // Cierra los menús hermanos
            }
          }
        }
      }
    }
  });

  // 2. SOLUCIÓN PARA EL TRACKING: Cierra menús hermanos automáticamente al cambiar de página
  const activeToggle = nav.querySelector(".md-nav__toggle[data-md-toggle='nav']:checked, .md-nav__item--active > .md-nav__toggle");
  if (activeToggle) {
    const parentLi = activeToggle.closest("li.md-nav__item--nested");
    if (parentLi) {
      const siblingLis = parentLi.parentElement.children;
      for (let sibling of siblingLis) {
        if (sibling !== parentLi) {
          const siblingToggle = sibling.querySelector(".md-nav__toggle");
          if (siblingToggle) {
            siblingToggle.checked = false; 
          }
        }
      }
    }
  }
}
