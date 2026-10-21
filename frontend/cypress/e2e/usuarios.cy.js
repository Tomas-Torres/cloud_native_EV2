describe('Usuarios API E2E Tests', () => {
  const baseUrl = 'http://localhost:8086/api/usuarios';
  
  it('Debería registrar un nuevo usuario', () => {
    const usuario = {
      nombre: 'Test User',
      email: 'test@example.com',
      password: 'password123'
    };
    
    cy.request('POST', `${baseUrl}/registro`, usuario)
      .should((response) => {
        expect(response.status).to.eq(201);
        expect(response.body).to.have.property('id');
        expect(response.body).to.have.property('email');
      });
  });

  it('Debería obtener un usuario por ID', () => {
    cy.request('GET', `${baseUrl}/1`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('id');
        expect(response.body).to.have.property('nombre');
      });
  });

  it('Debería actualizar un usuario', () => {
    const datosActualizados = {
      nombre: 'Usuario Actualizado'
    };
    
    cy.request('PUT', `${baseUrl}/1`, datosActualizados)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('nombre');
      });
  });
});
