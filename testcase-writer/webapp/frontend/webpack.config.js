const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpack = require('webpack');
const InterpolateHtmlPlugin = require('react-dev-utils/InterpolateHtmlPlugin');

module.exports = (env) => {

	return {
		entry: './src/index.tsx',
		output: {
			path: path.join(__dirname, '/target/frontend-dist'),
			filename: 'index_bundle.js'
		},
		devServer: {
			static: {
				directory: path.join(__dirname, 'public'),
			},
			compress: true,
			port: 9000,
			proxy: {
				'/api/websocket': {
					target: 'ws://localhost:8080',
					ws: true
				},
				'/api': {
					//target is where your proxy server is hosted
					target: 'http://localhost:8080',
					secure: 'false'
				}
			}
		},
		module: {
			rules: [
				{
					test: /\.tsx?$/,
					exclude: /node_modules/,
					loader: "babel-loader",
					options: {
						presets: [
							"@babel/preset-env",
							"@babel/preset-react",
							"@babel/preset-typescript",
						],
					},
				},
				{
					test: /\.css$/i,
					use: ["style-loader", "css-loader"],
				},
				{
					test: /\.(png|svg|jpg|jpeg|gif|ico)$/,
					exclude: /node_modules/,
					use: ['file-loader?name=[name].[ext]'] // ?name=[name].[ext] is only necessary to preserve the original file name
				}
			]
		},
		resolve: {
			extensions: [".js", ".jsx", ".ts", ".tsx"],
		},
		plugins: [
			new HtmlWebpackPlugin({
				template: './public/index.html'
			}),
			new webpack.DefinePlugin({
				'process.env.REACT_APP_API_URL': '\'api/v0\''
			}),
			new InterpolateHtmlPlugin(HtmlWebpackPlugin, {
				PUBLIC_URL: 'public'
			})
		]
	};
};
