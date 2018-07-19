const http = require('http')
const port = 3000
var finalhandler = require('finalhandler');
var serveStatic = require('serve-static');

var serve = serveStatic("./static");

const requestHandler = (req, res) => {
    var done = finalhandler(req, res);
    serve(req, res, done);
}

const server = http.createServer(requestHandler)

server.listen(port, (err) => {
  if (err) {
    return console.log('something bad happened', err)
  }

  console.log(`server is listening on ${port}`)
})