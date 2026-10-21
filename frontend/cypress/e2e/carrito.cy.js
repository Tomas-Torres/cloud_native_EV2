describe('Carrito API E2E Tests', () => {
  const baseUrl = 'http://localhost:8085/api/carrito';
  
  it('Debería obtener el carrito de un usuario', () => {
    cy.request('GET', `${baseUrl}/usuario/1`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });

  it('Debería agregar un item al carrito', () => {
    const item = {
      productoId: 1,
      cantidad: 2
    };
    
    cy.request('POST', `${baseUrl}/agregar`, item)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('id');
      });
  });

  it('Debería eliminar un item del carrito', () => {
    cy.request('DELETE', `${baseUrl}/item/1`)
      .should((response) => {
        expect(response.status).to.eq(204);
      });
  });
});
