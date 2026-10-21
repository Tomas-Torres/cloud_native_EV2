export const msalConfig = {
  auth: {
    clientId: '262adf80-2a1e-4193-a2bc-df28650eb1bc',
    authority: 'https://login.microsoftonline.com/f7592987-1fb4-4045-afea-34bb5a3fe9d7',
    redirectUri: 'http://localhost:5173',
  },
  cache: {
    cacheLocation: 'sessionStorage',
    storeAuthStateInCookie: false,
  },
};

// Scope de la API expuesta en el App Registration (Expose an API -> access_as_admin)
export const adminLoginRequest = {
  scopes: ['api://262adf80-2a1e-4193-a2bc-df28650eb1bc/access_as_admin'],
};