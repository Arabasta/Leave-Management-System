# Customizing

## Navigation Bar

### Adding

To add the navigation bar to your page, include the following line after the `<body>` tag in your HTML file:

```html

<div th:replace="fragments/navbar :: menu"></div>
```

Replace your `<head></head>` tag in your HTML file with:

```html

<head th:replace="fragments/head :: html_head"></head>
```

This will insert the navigation bar onto the top of your page, along with bootstrap and a title. Ignore the 'Missing
required title element warning by Intellij'.

\
`/home/admin` will look like this with the navbar:

```html
<!DOCTYPE html>
<html lang="en">
<head th:replace="fragments/head :: html_head"></head>

<body>
<div th:replace="fragments/navbar :: menu"></div>

Admin Home
</body>
</html>
```

### Customizing

Customize the navigation bar by editing the `navbar.html` file located at:

```
src/main/resources/templates/fragments/navbar.html
```

This file contains the HTML and Bootstrap classes for the navigation bar.
To change or add more navigation items, edit / add to the relevant sections in the `<ul class="navbar-nav">` tag.

```html
<body>
        <!-- Some code omitted -->
            <ul class="navbar-nav">
                <!-- Example of home/admin called Home for Admin Only -->
                <li class="nav-item pt-2" th:if="${#authorization.expression('hasRole(''ADMIN'')')}">
                    <a class="nav-link" th:href="@{/home/admin}">Home</a>
                </li>
                
                <!-- Example of home/manager called Home 1 for Manager Only -->
                <li class="nav-item pt-2" th:if="${#authorization.expression('hasRole(''MANAGER'')')}">
                    <a class="nav-link" th:href="@{/home/manager}">Home 1</a>
                </li>
            </ul>
</body>
```
