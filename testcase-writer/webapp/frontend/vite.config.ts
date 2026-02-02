import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
   build: {
     outDir: 'target/dist'
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:9000/',
      },
    },
  },
})
