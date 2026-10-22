describe('Bodega API E2E Tests', () => {
  const baseUrl = 'http://localhost:8081/api/bodega';
  
  it('Debería listar inventario', () => {
    cy.request('GET', `${baseUrl}/inventario`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });

  it('Debería obtener inventario por producto', () => {
    cy.request('GET', `${baseUrl}/inventario/producto/1`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('stock');
        expect(response.body).to.have.property('ubicacion');
      });
  });

  it('Debería actualizar stock', () => {
    const stockUpdate = {
      cantidad: 50
    };
    
    cy.request('PUT', `${baseUrl}/inventario/1/stock`, stockUpdate)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('stock');
      });
  });

  it('Debería listar alertas de stock', () => {
    cy.request('GET', `${baseUrl}/alertas`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });
});
