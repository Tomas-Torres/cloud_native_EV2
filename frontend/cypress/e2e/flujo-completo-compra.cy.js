describe('Flujo Completo de Compra - Integración entre Microservicios', () => {
  const bffBaseUrl = 'http://localhost:8080';
  const usuarioId = 1;
  
  it('Debería completar el flujo: Productos → Carrito → Pagos → Delivery', () => {
    // Paso 1: Listar productos disponibles
    cy.request(`${bffBaseUrl}/api/productos`)
      .should((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.be.an('array');
        expect(response.body.length).to.be.greaterThan(0);
      })
      .then((productosResponse) => {
        const producto = productosResponse.body[0];
        const productoId = producto.id;
        
        // Paso 2: Agregar producto al carrito
        const itemCarrito = {
          productoId: productoId,
          cantidad: 2
        };
        
        cy.request('POST', `${bffBaseUrl}/api/carrito/${usuarioId}/agregar`, itemCarrito)
          .should((response) => {
            expect(response.status).to.eq(200);
            expect(response.body).to.have.property('id');
          })
          .then((carritoResponse) => {
            const carritoItemId = carritoResponse.body.id;
            
            // Paso 3: Verificar que el producto está en el carrito
            cy.request(`${bffBaseUrl}/api/carrito/${usuarioId}`)
              .should((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.be.an('array');
                const itemEnCarrito = response.body.find(item => item.productoId === productoId);
                expect(itemEnCarrito).to.exist;
              });
            
            // Paso 4: Crear preferencia de pago
            const ordenId = `ORD-${Date.now()}`;
            const pagoRequest = {
              ordenId: ordenId,
              monto: producto.precio * 2,
              items: [
                {
                  productoId: productoId,
                  cantidad: 2,
                  precioUnitario: producto.precio
                }
              ]
            };
            
            cy.request('POST', `${bffBaseUrl}/api/pagos/crear-preferencia`, pagoRequest)
              .should((response) => {
                expect(response.status).to.eq(200);
                expect(response.body).to.have.property('preferenceId');
                expect(response.body).to.have.property('initPoint');
              })
              .then((pagoResponse) => {
                const preferenceId = pagoResponse.body.preferenceId;
                
                // Paso 5: Simular aprobación de pago (webhook)
                // En un escenario real, esto vendría de MercadoPago
                cy.request('POST', `${bffBaseUrl}/api/pagos/webhook`, {
                  preferenceId: preferenceId,
                  estado: 'aprobado'
                }).then(() => {
                  // Paso 6: Crear orden de delivery
                  const deliveryRequest = {
                    ordenId: ordenId,
                    direccion: 'Calle Principal 123',
                    ciudad: 'Santiago',
                    codigoPostal: '12345',
                    telefono: '+56912345678'
                  };
                  
                  cy.request('POST', `${bffBaseUrl}/api/delivery`, deliveryRequest)
                    .should((response) => {
                      expect(response.status).to.eq(201);
                      expect(response.body).to.have.property('id');
                      expect(response.body).to.have.property('estado', 'pendiente');
                    })
                    .then((deliveryResponse) => {
                      const deliveryId = deliveryResponse.body.id;
                      
                      // Paso 7: Verificar estado del delivery
                      cy.request(`${bffBaseUrl}/api/delivery/${deliveryId}`)
                        .should((response) => {
                          expect(response.status).to.eq(200);
                          expect(response.body).to.have.property('ordenId', ordenId);
                          expect(response.body).to.have.property('estado');
                        });
                      
                      // Paso 8: Actualizar estado del delivery
                      cy.request('PATCH', `${bffBaseUrl}/api/delivery/${deliveryId}/estado`, {
                        estado: 'en_transito'
                      })
                        .should((response) => {
                          expect(response.status).to.eq(200);
                          expect(response.body).to.have.property('estado', 'en_transito');
                        });
                      
                      // Paso 9: Limpiar - eliminar item del carrito
                      cy.request('DELETE', `${bffBaseUrl}/api/carrito/${usuarioId}/eliminar/${carritoItemId}`)
                        .should((response) => {
                          expect(response.status).to.eq(200);
                        });
                    });
                });
              });
          });
      });
  });

  it('Debería manejar error cuando producto no existe al agregar al carrito', () => {
    const itemCarrito = {
      productoId: 99999, // ID inexistente
      cantidad: 1
    };
    
    cy.request({
      method: 'POST',
      url: `${bffBaseUrl}/api/carrito/${usuarioId}/agregar`,
      body: itemCarrito,
      failOnStatusCode: false
    }).should((response) => {
      expect(response.status).to.be.oneOf([400, 404]);
    });
  });

  it('Debería manejar error cuando pago falla', () => {
    const pagoRequest = {
      ordenId: `ORD-INVALID-${Date.now()}`,
      monto: -100, // Monto inválido
      items: []
    };
    
    cy.request({
      method: 'POST',
      url: `${bffBaseUrl}/api/pagos/crear-preferencia`,
      body: pagoRequest,
      failOnStatusCode: false
    }).should((response) => {
      expect(response.status).to.be.oneOf([400, 500]);
    });
  });

  it('Debería obtener delivery por ordenId', () => {
    const ordenId = 'ORD-TEST-001';
    
    // Primero crear un delivery
    const deliveryRequest = {
      ordenId: ordenId,
      direccion: 'Avenida Test 456',
      ciudad: 'Santiago',
      codigoPostal: '12345',
      telefono: '+56987654321'
    };
    
    cy.request('POST', `${bffBaseUrl}/api/delivery`, deliveryRequest)
      .should((response) => {
        expect(response.status).to.eq(201);
      })
      .then(() => {
        // Obtener delivery por ordenId
        cy.request(`${bffBaseUrl}/api/delivery/orden/${ordenId}`)
          .should((response) => {
            expect(response.status).to.eq(200);
            expect(response.body).to.have.property('ordenId', ordenId);
          });
      });
  });
});
