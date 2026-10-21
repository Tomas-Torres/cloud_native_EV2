describe('Pagos API E2E Tests', () => {
  const baseUrl = 'http://localhost:8082/api/pagos';
  
  it('Debería crear preferencia de pago', () => {
    const preferencia = {
      ordenId: 'ORD-123',
      monto: 100.00,
      descripcion: 'Compra de productos'
    };
    
    cy.request('POST', `${baseUrl}/crear-preferencia`, preferencia)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('preferenceId');
      });
  });

  it('Debería procesar webhook de pago', () => {
    const webhookData = {
      topic: 'payment',
      resourceId: '12345'
    };
    
    cy.request('POST', `${baseUrl}/webhook`, webhookData)
      .should((response) => {
        expect(response.status).to.eq(200);
      });
  });

  it('Debería obtener estado de pago', () => {
    cy.request('GET', `${baseUrl}/estado/1`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('estado');
      });
  });
});
