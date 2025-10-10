<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!doctype html>
<html lang="ru">
<head>
  <meta charset="utf-8">
  <title>Результат проверки</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/style.css">
</head>
<body>

<table class="page">
  <tr>
    <td>
      <table class="header">
        <tr>
          <td class="logo-cell"><div class="logo">ITMO</div></td>
          <td class="title-cell">
            <div class="title">Web lab2</div>
            <div class="subtitle">Студент: Тенькаев Артём Антонович • Группа: P3231 • Вариант: 437</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>

  <tr>
    <td>
      <div class="content">
        <h2>Результат проверки</h2>

        <c:choose>
          <c:when test="${not empty attempt}">
            <table class="history-table">
              <tr><th>Параметр</th><th>Значение</th></tr>
              <tr><td>X</td><td>${attempt.x}</td></tr>
              <tr><td>Y</td><td>${attempt.y}</td></tr>
              <tr><td>R</td><td>${attempt.r}</td></tr>
              <tr>
                <td>Попадание</td>
                <td class="${attempt.hit ? 'hit-yes' : 'hit-no'}">
                  <c:out value="${attempt.hit ? 'Да' : 'Нет'}"/>
                </td>
              </tr>
              <tr><td>Время вычисления</td><td>${attempt.execMicros} мкс</td></tr>
              <tr><td>Момент проверки </td><td><fmt:formatDate value="${attempt.timestamp}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
            </table>

            <p><a href="${pageContext.request.contextPath}/controller" class="btn-primary">Новый запрос</a></p>
          </c:when>

          <c:otherwise>
            <p>Данные результата отсутствуют. Вернитесь на страницу формы.</p>
            <p><a href="${pageContext.request.contextPath}/controller" class="btn-primary">К форме</a></p>
          </c:otherwise>
        </c:choose>
      </div>
    </td>
  </tr>
</table>

</body>
</html>