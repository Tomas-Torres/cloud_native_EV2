describe('Productos API E2E Tests', () => {
  const baseUrl = 'http://localhost:8083/api/productos';
  
  it('Debería listar productos activos', () => {
    cy.request('GET', baseUrl)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });

  it('Debería obtener un producto por ID', () => {
    cy.request('GET', `${baseUrl}/1`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('id');
        expect(response.body).to.have.property('nombre');
      });
  });

  it('Debería buscar productos', () => {
    cy.request('GET', `${baseUrl}/buscar?q=zapatillas`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });

  it('Debería listar marcas', () => {
    cy.request('GET', `${baseUrl}/marcas`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });
});
