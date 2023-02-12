module.exports = {
    configureWebpack:{
    }, 
    devServer:{
      host: '0.0.0.0',
      hot:true,
      // https: true,
      port: 8081,  
      open: 'Chrome',
      proxy: { //https://cli.vuejs.org/guide/html-and-static-assets.html#disable-index-generation
        '/api/*':{ //everything from root
          target: 'http://localhost:8080',
          secure: false,
          ws: false,
        },
        '!/*':{ //except root, which is served by webpack's devserver, to faciliate instant updates
          target: 'http://localhost:8080/',
          secure: false,
          ws: false
        },
      }
    }
  }