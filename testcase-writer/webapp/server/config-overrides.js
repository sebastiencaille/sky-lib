const path = require('path');

module.exports = {
    paths: function (paths, env) {        
        paths.appSrc = path.resolve(__dirname, './src/main/react/src');
        paths.appIndexJs = path.resolve(paths.appSrc, 'index.js');
        paths.appPublic = path.resolve(__dirname, './src/main/react/public');
        paths.appHtml = path.resolve(paths.appPublic, 'index.html');
        return paths;
    },
}
