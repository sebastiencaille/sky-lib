import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
   build: {
     outDir: 'target/dist'
  },
  server: {
    proxy: {
		'/api/v0/websocket': {
		   target: 'http://localhost:9000/',
		   ws: true
		 },
      '/api': {
        target: 'http://localhost:9000/',
      }
   }
 
  }
})
