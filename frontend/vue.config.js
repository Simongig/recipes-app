module.exports = {
    configureWebpack:{
    }, 
    devServer:{
      // https: true,
      port: 8081,  
      open: 'Chrome',
      proxy: { //https://cli.vuejs.org/guide/html-and-static-assets.html#disable-index-generation
        '/*':{ //everything from root
          target: 'http://localhost:8080',
          secure: false,
          ws: false,
        },
      }
    }
  }