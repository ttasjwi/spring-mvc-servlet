package com.ttasjwi.servlet.web.frontcontroller.v5;

import com.ttasjwi.servlet.web.frontcontroller.ModelView;
import com.ttasjwi.servlet.web.frontcontroller.MyView;
import com.ttasjwi.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import com.ttasjwi.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import com.ttasjwi.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import com.ttasjwi.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import com.ttasjwi.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import com.ttasjwi.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import com.ttasjwi.servlet.web.frontcontroller.v5.adapter.ControllerV3Adapter;
import com.ttasjwi.servlet.web.frontcontroller.v5.adapter.ControllerV4Adapter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    private final Map<String, Object> handlerMappingMap = new HashMap<>();
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();

    public FrontControllerServletV5() {
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    private void initHandlerMappingMap() {
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
    }

    private void initHandlerAdapters() {
        handlerAdapters.add(new ControllerV3Adapter());
        handlerAdapters.add(new ControllerV4Adapter());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Object handler = getHandler(request);

        if (handler == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        ModelView mv = adapter.handle(request, response, handler);

        String viewName = mv.getViewName();
        MyView view = viewResolver(viewName);  // 논리이름을 경로명으로 변환한뒤 MyView 생성

        view.render(mv.getModel(), request, response); // 렌더링
    }

    private Object getHandler(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return handlerMappingMap.get(requestURI);
    }

    private MyHandlerAdapter getHandlerAdapter(Object handler) throws ServletException, IOException {
        for (MyHandlerAdapter adapter : handlerAdapters) {
            if (adapter.supports(handler)) {
                return adapter;
            }
        }
        throw new IllegalAccessError("HandlerAdapter를 찾을 수 없습니다. handler = " + handler);
    }

    private MyView viewResolver(String viewName) {
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }
}