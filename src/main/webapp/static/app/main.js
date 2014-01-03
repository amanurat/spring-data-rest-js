define({

	// Load a basic theme. This is just a CSS file, and since a moduleLoader is
	// configured in run.js, curl knows to load this as CSS.
	theme: { module: 'theme/basic.css' },

	// Create a simple view by rendering html, replacing some i18n strings
	// and loading CSS.  Then, insert into the DOM
	contactForm: {
		render: {
			template: { module: 'text!app/form/template.html' }
		},
		insert: { at: 'dom.first!body' },
		on: {
			'submit' : 'form.getValues | contactRepo.save'
		}
	},

	foo: {
		create: {
			module: 'app/foo',
			args: [ { $ref: 'contactRepo' } ]
		}
	},
	
	contactRepo: {
		create: {
			module: '/spring-data-rest-js/js/contact.js',
			args: [ { $ref: 'client' } ]
		}
	},
	
	client: {
		rest: [
			{ module: 'rest/interceptor/mime'}, 
			{ module: 'rest/interceptor/errorCode' }
		]
	},
	
	form: { module: 'cola/dom/form' },

	// Wire.js plugins
	plugins: [
		{ module: 'wire/dom', classes: { init: 'loading' } },
		{ module: 'wire/dom/render' },
		{ module: 'wire/on' },
		{ module: 'rest/wire' }
	]
});