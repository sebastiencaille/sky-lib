function computePath() {
  this.computePath = (element) => {
	const rightArrowParents = [];
	for (let elm = element; elm; elm = elm.parentNode) {
		let entry = elm.tagName.toLowerCase();
		if (entry === 'html') {
			break;
		}
		if (elm.id.trim()) { entry += '[id: ' + elm.id.trim() + '}'; }
		const className = elm.className.trim();
		if (className) {
			entry += '[.' + className.replace(/ +/g, '.') + ']';
		}
		rightArrowParents.push(entry);
	}
	rightArrowParents.reverse();
	return rightArrowParents.join('/');
  }
}
