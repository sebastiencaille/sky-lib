/**
 * Deploys a package using npm link
 */
const Os = require('os')
const fs = require('fs');
const { execFile } = require("child_process");

const copyPackage = (dir, version, cb) => {
	const filename = 'package.json';
	fs.readFile(filename, 'utf8', function(err, fileContent) {
		if (err) throw err;
		let result = fileContent.replace(/\$version\$/g, version);
		fs.writeFile(dir + filename, result, 'utf8', function(err) {
			if (err) throw err;
			cb();
		});
	});
}

const deploy = (dir, cb) => {
	const npm = (Os.platform() === 'win32') ? 'npm.cmd' : 'npm';
	const npmPack = execFile(npm, ['link'], { cwd: dir }, (error, stdout, stderr) => {
		if (error) {
			console.log(`error: ${error.message}`);
			return;
		}
		if (stderr) {
			console.log(`stderr: ${stderr}`);
			return;
		}
		console.log(`stdout: ${stdout}`);
	});

	npmPack.on('exit', (code) => {
		if (code === 0) {
			cb();
		} else {
			console.log(`code: ${code}`);
		}
	});
}

const writeIndex = (dir, cb) =>
	fs.writeFile(dir + '/index.js', `const { components } = require('./WebApis-generated-types.ts'); 
	exports.components = components
	`, {}, cb)

let dir = process.argv[2];
dir = dir + (dir.endsWith('/') || dir.endsWith('\\') ? '': '/');
console.info(`Deploying local package: ${dir}`);
const version = process.argv[3];
const thenDeploy = () => deploy(dir, () => { });
const thenCopyPackage = () => copyPackage(dir, version, thenDeploy);
writeIndex(dir, thenCopyPackage);
