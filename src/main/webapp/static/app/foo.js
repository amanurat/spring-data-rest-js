define(function(){
	
	function Foo(repo) {
		this._repo = repo;
		console.log("REST CLIENT:  ", repo);
		repo.save({});
	}
	
	return Foo;
	
});