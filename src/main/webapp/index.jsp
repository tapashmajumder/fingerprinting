<%--
  Created by IntelliJ IDEA.
  User: tapash.majumder
  Date: 7/26/18
  Time: 11:46 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Zee Title</title>
    <script src="https://unpkg.com/axios/dist/axios.min.js"></script>
    <script>
      function trackAndRedirect() {
          axios.post('/js/', {
              "id": document.getElementById('id-id').value,
              "browserFp" : createBrowserFp()
          }).then(function (response) {
              console.log('then:');
              console.log(response);
              window.location = document.getElementById('app-store-url-id').value;
          }).catch(function(error){
              console.log('error');
              console.log(error);
          });

      }

      function createBrowserFp() {
          var browserFp = {};
          browserFp.userAgent = "zee user agent";
          browserFp.screenWidth = 300;
          browserFp.screenHeight = 300;
          browserFp.osVersion = "11.4.2";
          browserFp.scale = 2.5;
          browserFp.userLocale = "en-US";
          browserFp.timeZoneOffset = 420;
          return browserFp;
      }
    </script>
  </head>
  <body onload="trackAndRedirect();">
  <input id='id-id' type="hidden" value='<%=request.getAttribute("id")%>'/>
  <input id='app-store-url-id' type="hidden" value='<%=request.getAttribute("app-store-url")%>'/>
  </body>
</html>
