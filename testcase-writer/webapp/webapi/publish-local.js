const fs = require('fs');
const { execFile } = require("child_process");

const copyPackage = (dir, cb) => 
  fs.copyFile('package.json', dir + '/package.json', (err) => {
    if (err) throw err;
    cb();
  });

const deploy = (dir, cb) => {
	const npmPack = execFile('npm', ['link'], { cwd: dir }, (error, stdout, stderr) => {
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
	
const dir = process.argv[2];
const thenDeploy = () => deploy(dir, () => {});
const thenCopyPackage = () => copyPackage(dir, thenDeploy);
writeIndex(dir, thenCopyPackage);
