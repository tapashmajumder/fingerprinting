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
          axios.post('/js/', createBrowserResponse())
          .then(function (response) {
              console.log('then:');
              console.log(response);
              window.location = document.getElementById('app-store-url-id').value;
          })
          .catch(function(error){
              console.log('error');
              console.log(error);
          });

      }

      function createBrowserResponse() {
          var browserResponse = {};
          browserResponse.id = document.getElementById('id-id').value;
          browserResponse.userAgent = navigator.userAgent;
          browserResponse.navigatorPlatform = navigator.platform;
          browserResponse.screenWidth = '' + window.screen.width;
          browserResponse.screenHeight = '' + window.screen.height;
          var scale = window.devicePixelRatio;
          if (typeof scale !== 'undefined') {
              browserResponse.scale = '' + scale;
          }
          browserResponse.navigatorLanguage = navigator.language;
          browserResponse.timezoneOffset = '' + new Date().getTimezoneOffset();
          return browserResponse;
      }
    </script>
  </head>
  <body onload="trackAndRedirect();">
  <input id='id-id' type="hidden" value='<%=request.getAttribute("id")%>'/>
  <input id='app-store-url-id' type="hidden" value='<%=request.getAttribute("app-store-url")%>'/>
  </body>
</html>
