describe('Ejemplo de prueba E2E', () => {
  it('Visita la página principal', () => {
    cy.visit('/');
    cy.contains('Lumina').should('be.visible');
  });
});
