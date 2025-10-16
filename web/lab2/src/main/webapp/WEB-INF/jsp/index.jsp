<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>


<!doctype html>
<html lang="ru">
<head>
  <meta charset="utf-8">
  <title>Web lab — Проверка попадания</title>
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
      <table class="content">
        <tr>
          <!-- форма -->
          <td class="col form-col">
            <form id="hit-form" method="POST" action="${pageContext.request.contextPath}/controller" novalidate>
              <table class="form-table">
                <tr><th colspan="2">Ввод данных</th></tr>

                <c:if test="${not empty error}">
                  <tr>
                    <td colspan="2">
                      <div id="msg" class="msg error"><c:out value="${error}"/></div>
                    </td>
                  </tr>
                </c:if>

                <tr>
                  <td class="label">X:</td>
                  <td>
                    <div class="x-buttons" id="x-buttons">
                      <button type="button" class="btn-x" data-x="-4">-4</button>
                      <button type="button" class="btn-x" data-x="-3">-3</button>
                      <button type="button" class="btn-x" data-x="-2">-2</button>
                      <button type="button" class="btn-x" data-x="-1">-1</button>
                      <button type="button" class="btn-x" data-x="0">0</button>
                      <button type="button" class="btn-x" data-x="1">1</button>
                      <button type="button" class="btn-x" data-x="2">2</button>
                      <button type="button" class="btn-x" data-x="3">3</button>
                      <button type="button" class="btn-x" data-x="4">4</button>
                    </div>
                    <input type="hidden" name="x" id="x-input">
                    <div class="hint">
                      Выберите одно значение X
                      <c:if test="${not empty errors.x}">
                        <span class="error">— ${errors.x}</span>
                      </c:if>
                    </div>
                  </td>
                </tr>

                <tr>
                  <td class="label">Y:</td>
                  <td>
                    <input type="text" name="y" id="y-input" placeholder="-3 … 3" class="text-input">
                    <div class="hint">
                      Число в диапазоне (-3; 3)
                      <c:if test="${not empty errors.y}">
                        <span class="error">— ${errors.y}</span>
                      </c:if>
                    </div>
                  </td>
                </tr>

                <tr>
                  <td class="label">R:</td>
                  <td>
                    <label class="check"><input type="checkbox" name="r" value="1"   class="r-check">1</label>
                    <label class="check"><input type="checkbox" name="r" value="1.5" class="r-check">1.5</label>
                    <label class="check"><input type="checkbox" name="r" value="2"   class="r-check">2</label>
                    <label class="check"><input type="checkbox" name="r" value="2.5" class="r-check">2.5</label>
                    <label class="check"><input type="checkbox" name="r" value="3"   class="r-check">3</label>
                    <div class="hint">
                      Выберите одно значение R
                      <c:if test="${not empty errors.r}">
                        <span class="error">— ${errors.r}</span>
                      </c:if>
                    </div>
                  </td>
                </tr>

                <tr>
                <td></td>
                                  <td>
                                    <button type="submit" id="submit-btn" class="btn-primary" disabled>Проверить</button>
                                    <button type="button" id="reset-btn" class="btn-ghost">Сбросить</button>
                                    <div id="msg" class="msg"><c:out value="${requestScope.message}"/></div>
                                  </td>
                                </tr>
                              </table>
                            </form>
                          </td>

                          <!-- графика -->
                          <td class="col graph-col">
                            <table class="graph-table">
                              <tr><th>Область</th></tr>
                              <tr><td class="canvas-cell"><canvas id="plot" width="420" height="420"></canvas></td></tr>
                            </table>
                            <div class="hint" id="graph-hint">
                              * При клике по графику координата X округляется до ближайшего целого из диапазона −4…4.
                            </div>
                          </td>

                          <!-- информация и история -->
                          <td class="col history-col">
                            <table class="info-table">
                              <tr><th>Информация</th></tr>
                              <tr>
                                <td>
                                  <div>Время вычисления последней проверки:
                                    <c:choose>
                                      <c:when test="${not empty sessionScope.lastExecMs}">
                                        <fmt:formatNumber value="${sessionScope.lastExecMs}" maxFractionDigits="3"/> мс
                                      </c:when>
                                      <c:otherwise>—</c:otherwise>
                                    </c:choose>
                                  </div>
                                </td>
                              </tr>
                            </table>

                            <table class="history-table" id="history-table">
                              <tr>
                                <th>#</th><th>X</th><th>Y</th><th>R</th><th>Попадание</th><th>Время</th>
                              </tr>
                              <c:forEach var="h" items="${sessionScope.hits}" varStatus="st">
                                <tr>
                                  <td>${st.count}</td>
                                  <td>${h.x}</td>
                                  <td>${h.y}</td>
                                  <td>${h.r}</td>
                                  <td class="${h.hit ? 'hit-yes' : 'hit-no'}">
                                    <c:out value="${h.hit ? 'да' : 'нет'}"/>
                                  </td>
                                  <td><fmt:formatDate value="${h.timestamp}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                                </tr>
                              </c:forEach>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <script>
                  window.initialHits = [
                    <c:forEach var="h" items="${sessionScope.hits}" varStatus="st">
                      { x: ${h.x}, y: ${h.y}, r: ${h.r}, hit: ${h.hit} }<c:if test="${!st.last}">,</c:if>
                    </c:forEach>
                  ];
                </script>


                <noscript>Для работы страницы нужен JavaScript.</noscript>
                <script defer src="${pageContext.request.contextPath}/static/js/app.js"></script>
                </body>
                </html>