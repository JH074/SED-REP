const fs = require('fs');
const path = require('path');
const url = require('url');
const database = require('./config/database.config');
const apiRouter = require('./routes/index.router'); // Enrutador principal

database.connect();

function createServerHandler() {
  return (req, res) => {
    const parsedUrl = url.parse(req.url, true);
    const pathname = parsedUrl.pathname;

    // Configurar las cabeceras para CORS y JSON
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Content-Type', 'application/json');

    // Rutas de la API
    if (pathname.startsWith('/api')) {
      // Eliminar el prefijo /api para que apiRouter maneje solo /account y /movies
      req.url = pathname.replace('/api', '');
      return apiRouter(req, res);
    } else if (pathname.startsWith('/public')) {
      // Manejo de archivos estáticos
      serveStaticFile(res, pathname);
    } else {
      // Respuesta 404 para rutas no encontradas
      res.statusCode = 404;
      res.end(JSON.stringify({ error: "Not Found" }));
    }
  };
}

function serveStaticFile(res, pathname) {
  const filePath = path.join(__dirname, pathname);
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.statusCode = 404;
      res.end("File not found");
    } else {
      res.statusCode = 200;
      res.end(data);
    }
  });
}

module.exports = createServerHandler;
