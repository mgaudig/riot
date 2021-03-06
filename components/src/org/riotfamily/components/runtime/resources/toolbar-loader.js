var riot = {
	contextPath: '${contextPath}',
	resourcePath: '${contextPath}${resourcePath}',
	path: '${contextPath}${riotServletPrefix}',
	language: '${language}' || 'en'
};

Resources.basePath = riot.resourcePath;
Resources.loadStyleSheet('style/toolbar.css');
Resources.loadStyleSheet('style/edit-mode.css');

Resources.loadScriptSequence([
	{src: 'dwr/engine.js', test: 'dwr.engine'},
	{src: 'dwr/util.js', test: 'dwr.util'},
	{src: 'dwr/interface/ComponentEditor.js', test: 'ComponentEditor', onload: loadToolbarScripts}
]);

function loadToolbarScripts() {
	var scripts = Resources.getRequiredSources([	
		{src: 'prototype/prototype.js', test: 'Prototype'},
		{src: 'riot-js/util.js', test: 'RElement'},
		{src: 'scriptaculous/effects.js', test: 'Effect'},
		{src: 'toolbar.js'},
		{src: 'scriptaculous/dragdrop.js', test: 'Droppables'},
		{src: 'riot-js/effects.js'},
		{src: 'riot-js/window-callback.js'},
		{src: 'inplace.js'},
		{src: 'components.js'}
	]);
	Resources.loadScript(riot.path + 
			'/joined/${riotVersion}/joined-script.js?files=' 
			+ scripts.join(',') + '&lang=' + riot.language, 'riot.toolbar', toolbarScriptsLoaded);
}

function toolbarScriptsLoaded() {
    if (window.onToolbarLoaded) {
    	onToolbarLoaded()
    }
    riot.toolbar.activate();
}