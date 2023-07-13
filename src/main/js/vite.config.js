import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'
import * as path from "path";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    build: {
        outDir: '..\\resources\\static'
    },
    resolve: {
        alias: {
            // '~bootstrap': path.resolve(__dirname, 'node_modules/bootstrap'),
        }
    }
})
