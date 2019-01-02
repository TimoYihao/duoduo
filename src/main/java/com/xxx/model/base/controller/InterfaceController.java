package com.xxx.model.base.controller;

import com.xxx.common.interfaces.NotLogin;
import com.xxx.common.util.PDDUtils;
import com.xxx.model.base.entity.*;
import com.xxx.model.base.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@NotLogin
@Controller
@RequestMapping(value = "api")
public class InterfaceController {

    @Resource
    private DdMemberService ddMemberService;
    @Resource
    private DdOrdersService ddOrdersService;
    @Resource
    private DdViptimeService ddViptimeService;
    @Resource
    private DdRebatelogService ddRebatelogService;
    @Resource
    private DdrankingmonitorService ddrankingmonitorService;

    //首页
    @RequestMapping("/")
    public String page(){
        return "index";
    }
    //个人首页
    @RequestMapping("center")
    public String center(){
        return "member-center/index";
    }
    //购买排名监控页面
    @RequestMapping("ranking_monitor")
    public String ranking_monitor(){
        return "member-center/ranking_monitor";
    }
    //购买查询排名页面
    @RequestMapping("check_rank")
    public String check_rank(){
        return "member-center/check_rank";
    }
    //邀请页面
    @RequestMapping("share_list")
    public String share_list(){
        return "member-center/share-list";
    }
    //购买记录
    @RequestMapping("buylist")
    public String buylist(){ return "member-center/buy-list"; }
    //修改密码
    @RequestMapping("setpwd")
    public String setpwd(){ return "member-center/set"; }
    //排名监控
    @RequestMapping("ranking")
    public String ranking(){ return "ranking"; }

    //登录
    @ResponseBody
    @RequestMapping(value = { "/login_pc" }, method = {RequestMethod.POST} )
    public Map<String, Object> sysLogin(String mphone, String password, HttpSession session) {
        DdMember ddMember = ddMemberService.sysrLogin(mphone, password);
        System.out.println("我进来了123");
        Map<String, Object> result = new HashMap<String, Object>();
        Integer days = 0;//声明排名监控服务剩余天数
        if (ddMember != null) {
            List<DdOrders> lo = null;
            String telephone = ddMember.getUserTelephone();
            lo = ddOrdersService.getOrdersByTelephone(telephone);
            DdMember ddMember1 = null;
            if(lo==null) {
                if(ddMember.getUserType()==2) {
                    ddMemberService.updateUserType(telephone,1);
                    ddMember1 = ddMemberService.sysrLogin(mphone, password);
                }
                session.setAttribute("days", days);
            }else {
                if(ddMember.getUserType()==1) {
                    ddMemberService.updateUserType(telephone,2);
                    ddMember1 = ddMemberService.sysrLogin(mphone, password);
                }
                days = ddViptimeService.selectDays(telephone);
                session.setAttribute("days", days);
                session.setAttribute("listOrder", lo);
            }
            if(ddMember1!=null) {
                session.setAttribute("user_login", ddMember1);
            }else {
                session.setAttribute("user_login", ddMember);
            }
            result.put("result", 1);
        }
        return result;
    }

    /**
     * 退出登录
     */
    @ResponseBody
    @RequestMapping(value = { "/exituser" })
    public Integer exitSystem(HttpSession session) {
        session.removeAttribute("user_login");
        session.removeAttribute("days");
        return 1;
    }

    @ResponseBody
    @RequestMapping(value = { "/subordinate" })
    public int subordinate(HttpSession session, @RequestParam(value="pn",required=false,defaultValue="1") int pn) {
        DdMember user = (DdMember)session.getAttribute("user_login");//获取登录用户信息
        Integer spnc = 0;//查询已邀请人充值的人数
        Integer spn = 0;//已邀请人数
        Integer sum = 0;
        List<Integer> count = null;//获得返利的金币和子级一一对应
        //List list = new ArrayList();
        if (user != null) {
            //查询已邀请人数
            spn = ddMemberService.selectPeopleNum(user.getId());
            com.xxx.model.base.entity.Page page;
            if(spn!=null) {
                Integer totalCount = spn;
                page = new com.xxx.model.base.entity.Page(pn, 10, totalCount);
            }else {
                page = new com.xxx.model.base.entity.Page(pn, 10, 0);
            }

            Integer user_id = user.getId();
            Map map = new HashMap();
            map.put("user_id", user_id);
            map.put("page", page);
            //查询下级
            List<DdMember> subordinate = ddMemberService.selectSubordinate(map);
            //如果有下级再执行
            if(subordinate.size()>0) {
                //查询已邀请人充值的人数
                spnc = ddMemberService.selectPeopleNumOrders(subordinate);
                count = new ArrayList<Integer>();
                for(DdMember u : subordinate) {
                    String telephone = u.getUserTelephone();
                    // 根据被邀请人查询记录
                    Integer number = ddRebatelogService.selectByTelephoneS(telephone);
                    if(number!=null) {
                        sum = sum + number;
                        count.add(number);
                    }else {
                        count.add(0);
                    }
                }
            }
            int no = 0;
            session.setAttribute("subordinate", subordinate);
            session.setAttribute("count", count);
            if(pn==1) {
                session.setAttribute("spnc", spnc);
                session.setAttribute("spn", spn);
                session.setAttribute("sum", sum);
                session.setAttribute("page", page);
                session.setAttribute("no", no);
            }else {
                no=(pn-1)*10;
                session.setAttribute("no", no);
            }
        }
        //return "/share_list";
        return 1;
    }

    /**
     * 前台查询个人订单
     */
    @RequestMapping(value = { "/selectOrders" })
    @ResponseBody
    public int selectOrders(HttpSession session, @RequestParam(value = "pn", required = false, defaultValue = "1") int pn) {
        DdMember user = (DdMember) session.getAttribute("user_login");
        String telephone = user.getUserTelephone();
        Integer count = ddOrdersService.getOrdersCountByPhone(telephone);
        Page page;
        if (count != null) {
            Integer totalCount = count;
            page = new Page(pn, 10, totalCount);
        } else {
            page = new Page(pn, 10, 0);
        }
        Map map = new HashMap();
        map.put("telephone", telephone);
        map.put("page", page);
        List<DdOrders> listOrders = ddOrdersService.getOrdersByTelephoneForPage(map);
        int no = 0;
        if (pn == 1) {
            session.setAttribute("orderno", no);
        } else {
            no = (pn - 1) * 10;
            session.setAttribute("orderno", no);
        }
        session.setAttribute("listOrders", listOrders);
        session.setAttribute("orderpage", page);
        return 1;
    }

    //修改密码
    @ResponseBody
    @RequestMapping(value="toUpdate")
    public int toUpdate(String userTelephone,String new_password,String userPassword){
        if(ddMemberService.sysrLogin(userTelephone,userPassword)!=null){
            int i = ddMemberService.updateUserPassword(new_password,userTelephone);
            return i;
        }else{
            return 0;
        }
    }

    /**
     * 注册
     */
    @ResponseBody
    @RequestMapping(value= {"sysRegister"},method = { RequestMethod.POST })
    public int sysRegister(String user_telephone,
                              String user_password,
                              HttpSession session,
                              @RequestParam(value="user_code",defaultValue="123",required=false)String user_code) {
        DdMember user1 = ddMemberService.sysLoginTelephone(user_telephone);
        if(user1==null) {
            Integer user_id = null;
            if(!user_code.equals("123")) {
                // 根据邀请码查询ID
                user_id = ddMemberService.selectIDByCode(user_code);
            }
            //修改注册
            ddMemberService.addRegister(user_telephone, user_password,user_id);
            DdMember user2 = ddMemberService.sysrLogin(user_telephone, user_password);
            System.out.println("user2="+user2);
            String usercode = "ddms"+user2.getId();
            ddMemberService.updateCode(user_telephone, user_password, usercode);
            DdMember user3 = ddMemberService.sysrLogin(user_telephone, user_password);
            System.out.println("user3="+user3);
            if (user3 != null) {
                session.setAttribute("user_login", user3);
                return 0;
            }else {
                return 2;
            }
        }else {
            return 1;
        }
    }
        // 查询vip服务价格
        @ResponseBody
        @RequestMapping("fandMoney")
        public Ddrankingmonitor fandMoney(Integer serviceCycle, Integer type) {
            //Map<String, String> map = rankingMonitorService.fandMoney(serviceCycle, type);
            Ddrankingmonitor rankingMonitor = ddrankingmonitorService.fandMoney(serviceCycle, type);

            return rankingMonitor;
        }

    @ResponseBody
    @RequestMapping(value = "getKey",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    public Map getKey(@RequestParam String keyword,@RequestParam String goodsId,@RequestParam String sort){
        Map<String, Object> map = new HashMap<>();
        try {
            List<Map> list = PDDUtils.getGoodsList(keyword,sort);
            for (int i = 1; i < list.size(); i++) {
                Map p = list.get(i);
                if(p.get("goods_id").toString().equals(goodsId)){
                    Map<String,Object> m = new HashMap<>();
                    m.put("name",p.get("goods_name"));
                    m.put("group",p.get("min_group_price"));
                    m.put("normal",p.get("min_normal_price"));
                    m.put("sold",p.get("sold_quantity"));
                    m.put("img",p.get("goods_thumbnail_url"));
                    m.put("top",i);
                    map.put("goods",m);
                    System.out.println("商品信息："+p.get("goods_name")+"~~~团购价："+p.get("min_group_price")+"~~~单价："+p.get("min_normal_price")+"~~~销量："+p.get("sold_quantity")+"~~~排名："+i);
                    break;
                }
            }
            map.put("success","000");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}