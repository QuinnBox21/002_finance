const path = require('path')

module.exports = {
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      }
    }
  },
  devServer: {
    port: 5173,
    proxy: {
      '/api': {
        target: process.env.VUE_APP_API_BASE_URL,
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  },
  outputDir: 'dist',
  assetsDir: 'assets',
  productionSourceMap: false,
  chainWebpack: config => {
    config.optimization.splitChunks({
      chunks: 'all',
      cacheGroups: {
        vendor: {
          test: /\/node_modules\/(vue|vue-router|pinia|axios|element-plus)\//,
          name: 'vendor',
          chunks: 'all'
        },
        echarts: {
          test: /\/node_modules\/echarts\//,
          name: 'echarts',
          chunks: 'all'
        }
      }
    })
  }

}