describe('Delivery API E2E Tests', () => {
  const baseUrl = 'http://localhost:8084/api/delivery';
  
  it('Debería listar deliveries activos', () => {
    cy.request('GET', `${baseUrl}/activos`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });

  it('Debería obtener delivery por ID', () => {
    cy.request('GET', `${baseUrl}/1`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('id');
        expect(response.body).to.have.property('estado');
      });
  });

  it('Debería crear nuevo delivery', () => {
    const delivery = {
      ordenId: 'ORD-123',
      direccion: 'Calle Test 123',
      clienteId: 1
    };
    
    cy.request('POST', `${baseUrl}/crear`, delivery)
      .should((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
      });
  });

  it('Debería actualizar estado de delivery', () => {
    const estadoUpdate = {
      estado: 'EN_REPARTO'
    };
    
    cy.request('PUT', `${baseUrl}/1/estado`, estadoUpdate)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('estado');
      });
  });

  it('Debería listar historial de delivery', () => {
    cy.request('GET', `${baseUrl}/1/historial`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
      });
  });
});
