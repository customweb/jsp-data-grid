jsp-data-grid
=============

This JSP Tag library provides an easy way to create data grids with Java and JSP. It provides an easy integration with the Spring framework and Java Persistence API (JPA). Optionally also some Ajax functionality can be attached to the grid.

There are a lot of different JSP grid implementation. Some of them are jmesa or display tags.

This implementation does differ primarily by:
- Works also without JavaScript.
- Easy adapter for the Java Persistence API (JPA).
- The paging does only query the items, that are really required. This is a inherit part of the library.
- The filter for rows are generated based on java reflections. They get adapted automatically to the actual column type.
- Relations get loaded automatically.
- All parameters for the Grid are handled in the URL.
- AJAX features can be added optionally.
- AJAX does not require to implement any additional server handlers.
- Easy integration with Spring MVC.

### Samples

#### JSP File
```
<g:grid grid="${grid}" class="ajax-pane grid">
        <!-- Grid with Column -->
        <g:table var="item" showFilterRow="true">
                <g:column fieldName="profileId" title="#" sortable="true" filterable="true"></g:column>
                <g:column fieldName="name" title="Name" sortable="true" filterable="true"></g:column>
                <g:column fieldName="user.email" title="User" sortable="true" filterable="true"></g:column>
        </g:table>
        
        <!-- A limit dropdown -->
        <g:limit steps="10,20,50,100" class="grid-limit" />
        
        <!-- Button to apply filter etc. -->
        <g:submit value="Apply" class="btn"/>

        <!-- Pager to navigate between pages -->
        <g:pager class="pagination pagination-centered" previousNextButton="true"  startEndButton="true"/>
        
        
</g:grid>
```

#### Spring MVC Controller
```
 public ModelAndView editUser(HttpServletRequest request) throws MalformedURLException {
                ModelAndView model = new ModelAndView("user-list.jsp");         
                
                // Set the current request to build the Grid
                RequestFilterBuilder filterBuilder = new RequestFilterBuilder(request);
                
                // Setup the connection the Database 
                Executor<User> executor = new JavaPersistenceApiExecutor<User>(entityManager, User.class);
                
                // Setup the Grid with the indicated configuration
                Grid<User> grid = new Grid<User>("user_list", executor, filterBuilder);
                grid.setUrl(request);
                grid.prepare();
                
                // Add the grid to the model; provide it to the JSP page
                model.addObject("grid", grid);
                
                return model;
        }
```

### AJAX
In the provided jar is a .CSS file and a JavaScript? included. Only a minimal styling is provided. You 
can use for example Bootstrap from twitter to apply styles. The JavaScript? file provides an Ajax handler.
If you add the css class "ajax-pane" to the grid div tag, then the whole grid becomes ajaxified. You need 
also including jquery. The ajax feature is a simple HTTP get call to the generated URL. The div container 
is replaced with the response from the server. It is not a classic ajax implementation, but it has two major 
advantage no extra work on the server side has to be done and the ajax feature can be simply attached by a 
css class.





