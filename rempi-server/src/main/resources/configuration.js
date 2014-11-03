// dedicated variable
var configuration = {

    application:{
        
        encoding : "UTF-8",
        
        modules : {
        	'com.codecity.web.modules.auth' : 'authentication', // defined alias of module
        	'some.path.to.module.package' : {
        		moduleAlias : 'foo', // dedicated property, same as string value for module package
        		property : 'value of property'
        	}
        },
        
        components : {
            
            viewFactory : {
                viewFileSuffix : '.wdgt'
            }
            
        }
    }    
};