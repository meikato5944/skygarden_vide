package com.example.skygarden.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.skygarden.bean.DirectoryNodeBean;
import com.example.skygarden.constants.Constants;
import com.example.skygarden.logic.Content;
import com.example.skygarden.logic.Setting;
import com.example.skygarden.util.ScreenNameConverter;

import jakarta.servlet.http.HttpSession;

/**
 * フロントエンド画面のルーティングを処理するコントローラー
 * 
 * このコントローラーはThymeleafテンプレートを使用したサーバーサイドレンダリングを提供します。
 * 各エンドポイントはHTMLページを返し、モデルにデータをバインドしてビューに渡します。
 * 
 * 主な画面:
 * - ログイン画面（/login）
 * - コンテンツ一覧画面（/）
 * - コンテンツ編集画面（/content）
 * - ユーザー一覧画面（/user-list）
 * - ユーザー編集画面（/user）
 * - 設定画面（/setting）
 * - URLディレクトリ画面（/url-directory）
 * 
 * @see Content コンテンツ管理のビジネスロジック
 * @see Setting 設定管理のビジネスロジック
 * @see com.example.skygarden.logic.User ユーザー管理のビジネスロジック
 */
@Controller
public class HomeController {
    
    /** コンテンツ管理のビジネスロジック */
    @Autowired
    private Content content;
    
    /** 設定管理のビジネスロジック */
    @Autowired
    private Setting setting;
    
    /** ユーザー管理のビジネスロジック */
    @Autowired
    private com.example.skygarden.logic.User user;
    
    /**
     * ログイン画面のルーティング
     * 
     * @return login.html
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * トップページ（コンテンツ一覧）のルーティング
     * キーワード検索にも対応
     * 
     * @param mode モード
     * @param sort ソート順
     * @param page ページ番号
     * @param keyword 検索キーワード（オプション）
     * @param model モデル
     * @param session セッション
     * @return list.html
     */
    @GetMapping("/")
    public String index(@RequestParam(required = false, defaultValue = "") String mode,
                       @RequestParam(required = false, defaultValue = "") String sort,
                       @RequestParam(required = false, defaultValue = "1") String page,
                       @RequestParam(required = false, defaultValue = "") String keyword,
                       Model model, HttpSession session) {
        try {
            String[][] sortOptions = Constants.SORT_OPTIONS_CONTENT;
            StringBuffer sortOutput = new StringBuffer();
            String registerMessage = Constants.EMPTY_STRING;
            int thisPage = 1;
            
            if (session.getAttribute(Constants.SESSION_REGISTER_MESSAGE) != null 
                    && !session.getAttribute(Constants.SESSION_REGISTER_MESSAGE).equals(Constants.EMPTY_STRING)) {
                registerMessage = (String) session.getAttribute(Constants.SESSION_REGISTER_MESSAGE);
                session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
            }
            try {
                thisPage = Integer.valueOf(page);
            } catch (NumberFormatException e) {
                thisPage = 1;
            }
            
            // 画面名の設定
            String screenName = ScreenNameConverter.convertModeToScreenName(mode);
            
            // ソートオプションの生成
            for (int i = 0; i < sortOptions.length; i++) {
                String selected = "";
                if (sortOptions[i][0].equals(sort)) {
                    selected = "selected";
                }
                sortOutput.append("<option value=\"" + sortOptions[i][0] + "\" " + selected + ">" + sortOptions[i][1] + "</option>");
            }
            
            List<HashMap<String, String>> results;
            String pagerOutput;
            
            // キーワードが指定されている場合は検索、そうでない場合は通常の一覧取得
            if (keyword != null && !keyword.trim().isEmpty()) {
                results = content.searchList(sort, thisPage, mode, keyword);
                pagerOutput = content.getSearchPager(thisPage, mode, sort, keyword);
            } else {
                results = content.getList(sort, thisPage, mode);
                pagerOutput = content.getPager(thisPage, mode, sort);
            }
            
            model.addAttribute("mode", mode);
            model.addAttribute("sort", sort);
            model.addAttribute("keyword", keyword);
            model.addAttribute("screenName", screenName);
            model.addAttribute("registerMessage", registerMessage);
            model.addAttribute("results", results != null ? results : new java.util.ArrayList<>());
            model.addAttribute("pagerOutput", pagerOutput != null ? pagerOutput : "");
            model.addAttribute("sortOptions", sortOptions);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("screenName", Constants.SCREEN_NAME_CONTENT);
            model.addAttribute("registerMessage", Constants.EMPTY_STRING);
            model.addAttribute("keyword", Constants.EMPTY_STRING);
            model.addAttribute("results", new java.util.ArrayList<>());
            model.addAttribute("pagerOutput", Constants.EMPTY_STRING);
            model.addAttribute("sortOptions", Constants.SORT_OPTIONS_CONTENT);
        }
        return "list";
    }
    
    /**
     * コンテンツ編集画面のルーティング
     * モードに応じて適切なテンプレートを返す
     * 
     * @param mode モード（空:コンテンツ、template:テンプレート、element:構成要素）
     * @param id コンテンツID
     * @param model モデル
     * @return モードに応じたテンプレート
     */
    @GetMapping("/content")
    public String content(@RequestParam(required = false, defaultValue = "") String mode,
                         @RequestParam(required = false, defaultValue = "") String id,
                         Model model) {
        try {
            String title = "";
            String template = "";
            String head = "";
            String contentStr = "";
            String url = "";
            String elementcolor = "";
            String schedule_published = "";
            String schedule_unpublished = "";
            String publishflg_keep = "";
            
            if (!id.equals(Constants.EMPTY_STRING)) {
                // 既存コンテンツの編集
                HashMap<String, String> result = content.doSearch(id);
                if (result != null) {
                    template = result.get("template") != null ? result.get("template") : "";
                    title = result.get("title") != null ? result.get("title") : "";
                    head = result.get("head") != null ? result.get("head") : "";
                    contentStr = result.get("content") != null ? result.get("content") : "";
                    url = result.get("url") != null ? result.get("url") : "";
                    elementcolor = result.get("elementcolor") != null ? result.get("elementcolor") : "";
                    schedule_published = result.get("schedule_published") != null ? result.get("schedule_published") : "";
                    schedule_unpublished = result.get("schedule_unpublished") != null ? result.get("schedule_unpublished") : "";
                    publishflg_keep = result.get("publishflg_keep") != null ? result.get("publishflg_keep") : "";
                }
            } else {
                // 新規作成時はデフォルト公開設定を適用
                publishflg_keep = setting.getDefaultPublishOn();
            }
            
            // 共通属性を設定
            model.addAttribute("id", id);
            model.addAttribute("title", title);
            model.addAttribute("head", head);
            model.addAttribute("contentStr", contentStr);
            model.addAttribute("url", url);
            
            // モードに応じて処理を分岐
            if (mode.equals(Constants.CONTENT_TYPE_TEMPLATE)) {
                // テンプレートモード
                return handleTemplateMode(id, contentStr, model);
            } else if (mode.equals(Constants.CONTENT_TYPE_ELEMENT)) {
                // 構成要素モード
                return handleElementMode(elementcolor, model);
            } else if (mode.equals(Constants.CONTENT_TYPE_IMAGE)) {
                // 画像モード
                return handleImageMode(head, schedule_published, schedule_unpublished, publishflg_keep, model);
            } else if (mode.equals(Constants.CONTENT_TYPE_FILE)) {
                // ファイルモード
                return handleFileMode(schedule_published, schedule_unpublished, publishflg_keep, model);
            } else if (mode.equals(Constants.CONTENT_TYPE_MOVIE)) {
                // 動画モード
                return handleMovieMode(head, schedule_published, schedule_unpublished, publishflg_keep, model);
            } else {
                // コンテンツモード（デフォルト）
                return handleContentMode(template, schedule_published, schedule_unpublished, publishflg_keep, model);
            }
        } catch (Exception e) {
            e.printStackTrace();
            setDefaultModelAttributes(model, mode);
            return getTemplateByMode(mode);
        }
    }
    
    /**
     * コンテンツモードの処理
     */
    private String handleContentMode(String template, String schedule_published, 
            String schedule_unpublished, String publishflg_keep, Model model) {
        StringBuffer templateOutput = new StringBuffer();
        List<HashMap<String, String>> templateResults = content.getList(Constants.EMPTY_STRING, 1, Constants.CONTENT_TYPE_TEMPLATE);
        templateOutput.append("<option value=\"" + Constants.EMPTY_STRING + "\">--none--</option>");
        for (HashMap<String, String> templateResult : templateResults) {
            String templateId = templateResult.get("id");
            String templateTitle = templateResult.get("title");
            String selected = templateId.equals(template) ? " selected" : "";
            templateOutput.append("<option value=\"" + templateId + "\"" + selected + ">" + templateTitle + "</option>");
        }
        
        model.addAttribute("templateOutput", templateOutput.toString());
        model.addAttribute("schedule_published", schedule_published);
        model.addAttribute("schedule_unpublished", schedule_unpublished);
        model.addAttribute("publishflg_keep", publishflg_keep);
        
        return "content-edit";
    }
    
    /**
     * テンプレートモードの処理
     */
    private String handleTemplateMode(String id, String contentStr, Model model) {
        List<HashMap<String, String>> eleResults = new java.util.ArrayList<>();
        
        if (!id.equals(Constants.EMPTY_STRING) && !contentStr.equals(Constants.EMPTY_STRING)) {
            String[] elements = contentStr.split(",");
            for (String element : elements) {
                if (element.equals(Constants.TEMPLATE_CONTENT_PLACEHOLDER)) {
                    HashMap<String, String> m = new HashMap<>();
                    m.put("id", "content");
                    m.put("title", "");
                    m.put("code", "");
                    eleResults.add(m);
                } else {
                    String elementId = element.replace(Constants.ELEMENT_PREFIX, Constants.EMPTY_STRING)
                            .replace(Constants.ELEMENT_FUNCTION_START, Constants.EMPTY_STRING)
                            .replace(Constants.ELEMENT_FUNCTION_END, Constants.EMPTY_STRING);
                    HashMap<String, String> eleResult = content.doSearch(elementId);
                    if (eleResult != null) {
                        HashMap<String, String> m = new HashMap<>();
                        m.put("id", eleResult.get("id"));
                        m.put("title", eleResult.get("title"));
                        m.put("code", eleResult.get("elementcolor"));
                        eleResults.add(m);
                    }
                }
            }
        } else {
            HashMap<String, String> m = new HashMap<>();
            m.put("id", "content");
            m.put("title", "");
            m.put("code", "");
            eleResults.add(m);
        }
        
        List<HashMap<String, String>> elementItems = content.getAllList(Constants.TABLE_CONTENT, Constants.EMPTY_STRING, Constants.CONTENT_TYPE_ELEMENT);
        
        model.addAttribute("eleResults", eleResults);
        model.addAttribute("elementItems", elementItems != null ? elementItems : new java.util.ArrayList<>());
        
        return "template-edit";
    }
    
    /**
     * 構成要素モードの処理
     */
    private String handleElementMode(String elementcolor, Model model) {
        StringBuffer colorOutput = new StringBuffer();
        List<HashMap<String, String>> colorElements = setting.elementsColorList();
        colorOutput.append("<option value=\"" + Constants.EMPTY_STRING + "\">--none--</option>");
        for (HashMap<String, String> colorElement : colorElements) {
            String code = colorElement.get("code");
            String name = colorElement.get("name");
            String selected = code.equals(elementcolor) ? " selected" : "";
            colorOutput.append("<option value=\"" + code + "\" style=\"background-color:" + code + ";\"" + selected + ">" + name + "</option>");
        }
        
        model.addAttribute("colorOutput", colorOutput.toString());
        model.addAttribute("elementcolor", elementcolor);
        
        return "element-edit";
    }
    
    /**
     * 画像モードの処理
     */
    private String handleImageMode(String head, String schedule_published, String schedule_unpublished, 
            String publishflg_keep, Model model) {
        model.addAttribute("schedule_published", schedule_published);
        model.addAttribute("schedule_unpublished", schedule_unpublished);
        model.addAttribute("publishflg_keep", publishflg_keep);
        
        // headフィールドからサイズ情報を抽出
        String imageWidth = "";
        String imageHeight = "";
        if (head != null && !head.isEmpty() && head.startsWith("{")) {
            // 簡易的なJSON解析
            if (head.contains("\"width\":\"")) {
                int start = head.indexOf("\"width\":\"") + 9;
                int end = head.indexOf("\"", start);
                if (end > start) {
                    imageWidth = head.substring(start, end);
                }
            }
            if (head.contains("\"height\":\"")) {
                int start = head.indexOf("\"height\":\"") + 10;
                int end = head.indexOf("\"", start);
                if (end > start) {
                    imageHeight = head.substring(start, end);
                }
            }
        }
        model.addAttribute("imageWidth", imageWidth);
        model.addAttribute("imageHeight", imageHeight);
        
        return "image-edit";
    }
    
    /**
     * ファイルモードの処理
     */
    private String handleFileMode(String schedule_published, String schedule_unpublished, 
            String publishflg_keep, Model model) {
        model.addAttribute("schedule_published", schedule_published);
        model.addAttribute("schedule_unpublished", schedule_unpublished);
        model.addAttribute("publishflg_keep", publishflg_keep);
        
        return "file-edit";
    }
    
    /**
     * 動画モードの処理
     */
    private String handleMovieMode(String head, String schedule_published, String schedule_unpublished, 
            String publishflg_keep, Model model) {
        model.addAttribute("schedule_published", schedule_published);
        model.addAttribute("schedule_unpublished", schedule_unpublished);
        model.addAttribute("publishflg_keep", publishflg_keep);
        
        // headフィールドからサイズ情報を抽出
        String movieWidth = "";
        String movieHeight = "";
        if (head != null && !head.isEmpty() && head.startsWith("{")) {
            // 簡易的なJSON解析
            if (head.contains("\"width\":\"")) {
                int start = head.indexOf("\"width\":\"") + 9;
                int end = head.indexOf("\"", start);
                if (end > start) {
                    movieWidth = head.substring(start, end);
                }
            }
            if (head.contains("\"height\":\"")) {
                int start = head.indexOf("\"height\":\"") + 10;
                int end = head.indexOf("\"", start);
                if (end > start) {
                    movieHeight = head.substring(start, end);
                }
            }
        }
        model.addAttribute("movieWidth", movieWidth);
        model.addAttribute("movieHeight", movieHeight);
        
        return "movie-edit";
    }
    
    /**
     * モードに応じたテンプレート名を返す
     */
    private String getTemplateByMode(String mode) {
        if (Constants.CONTENT_TYPE_TEMPLATE.equals(mode)) {
            return "template-edit";
        } else if (Constants.CONTENT_TYPE_ELEMENT.equals(mode)) {
            return "element-edit";
        } else if (Constants.CONTENT_TYPE_IMAGE.equals(mode)) {
            return "image-edit";
        } else if (Constants.CONTENT_TYPE_FILE.equals(mode)) {
            return "file-edit";
        } else if (Constants.CONTENT_TYPE_MOVIE.equals(mode)) {
            return "movie-edit";
        } else {
            return "content-edit";
        }
    }
    
    /**
     * エラー時のデフォルト属性を設定
     */
    private void setDefaultModelAttributes(Model model, String mode) {
        model.addAttribute("id", Constants.EMPTY_STRING);
        model.addAttribute("title", Constants.EMPTY_STRING);
        model.addAttribute("head", Constants.EMPTY_STRING);
        model.addAttribute("contentStr", Constants.EMPTY_STRING);
        model.addAttribute("url", Constants.EMPTY_STRING);
        model.addAttribute("templateOutput", Constants.EMPTY_STRING);
        model.addAttribute("colorOutput", Constants.EMPTY_STRING);
        model.addAttribute("elementcolor", Constants.EMPTY_STRING);
        model.addAttribute("schedule_published", Constants.EMPTY_STRING);
        model.addAttribute("schedule_unpublished", Constants.EMPTY_STRING);
        model.addAttribute("publishflg_keep", Constants.EMPTY_STRING);
        model.addAttribute("eleResults", new java.util.ArrayList<>());
        model.addAttribute("elementItems", new java.util.ArrayList<>());
    }
    
    /**
     * ユーザー一覧画面のルーティング
     * 
     * @param sort ソート順
     * @param page ページ番号
     * @param model モデル
     * @param session セッション
     * @return user-list.html
     */
    @GetMapping("/user-list")
    public String userList(@RequestParam(required = false, defaultValue = Constants.EMPTY_STRING) String sort,
                          @RequestParam(required = false, defaultValue = "1") String page,
                          Model model, HttpSession session) {
        try {
            String[][] sortOptions = Constants.SORT_OPTIONS_USER;
            String registerMessage = Constants.EMPTY_STRING;
            int thisPage = 1;
            
            if (session.getAttribute(Constants.SESSION_REGISTER_MESSAGE) != null 
                    && !session.getAttribute(Constants.SESSION_REGISTER_MESSAGE).equals(Constants.EMPTY_STRING)) {
                registerMessage = (String) session.getAttribute(Constants.SESSION_REGISTER_MESSAGE);
                session.setAttribute(Constants.SESSION_REGISTER_MESSAGE, Constants.EMPTY_STRING);
            }
            try {
                thisPage = Integer.valueOf(page);
            } catch (NumberFormatException e) {
                thisPage = 1;
            }
            
            List<HashMap<String, String>> results = user.getList(sort, thisPage);
            String pagerOutput = user.getPager(thisPage, Constants.PATH_USER_LIST, sort);
            
            model.addAttribute("sort", sort);
            model.addAttribute("registerMessage", registerMessage);
            model.addAttribute("results", results != null ? results : new java.util.ArrayList<>());
            model.addAttribute("pagerOutput", pagerOutput != null ? pagerOutput : Constants.EMPTY_STRING);
            model.addAttribute("sortOptions", sortOptions);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("registerMessage", Constants.EMPTY_STRING);
            model.addAttribute("results", new java.util.ArrayList<>());
            model.addAttribute("pagerOutput", Constants.EMPTY_STRING);
            model.addAttribute("sortOptions", Constants.SORT_OPTIONS_USER);
        }
        return "user-list";
    }
    
    /**
     * ユーザー編集画面のルーティング
     * 
     * @param id ユーザーID
     * @param model モデル
     * @return user.html
     */
    @GetMapping("/user")
    public String user(@RequestParam(required = false, defaultValue = Constants.EMPTY_STRING) String id, Model model) {
        try {
            String name = Constants.EMPTY_STRING;
            String password = Constants.EMPTY_STRING;
            String email = Constants.EMPTY_STRING;
            String admin = Constants.FLAG_NO;
            
            if (!id.equals(Constants.EMPTY_STRING)) {
                HashMap<String, String> result = user.getUser(id);
                if (result != null) {
                    name = result.get("name") != null ? result.get("name") : Constants.EMPTY_STRING;
                    password = result.get("password") != null ? result.get("password") : Constants.EMPTY_STRING;
                    email = result.get("email") != null ? result.get("email") : Constants.EMPTY_STRING;
                    admin = result.get("admin") != null ? result.get("admin") : Constants.FLAG_NO;
                }
            }
            
            model.addAttribute("id", id);
            model.addAttribute("name", name);
            model.addAttribute("password", password);
            model.addAttribute("email", email);
            model.addAttribute("admin", admin);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("id", id);
            model.addAttribute("name", Constants.EMPTY_STRING);
            model.addAttribute("password", Constants.EMPTY_STRING);
            model.addAttribute("email", Constants.EMPTY_STRING);
            model.addAttribute("admin", Constants.FLAG_NO);
        }
        return "user";
    }
    
    /**
     * 設定画面のルーティング
     * 
     * @param model モデル
     * @return setting.html
     */
    @GetMapping("/setting")
    public String setting(Model model) {
        try {
            List<HashMap<String, String>> colorElements = setting.elementsColorList();
            String defaultPublishOn = setting.getDefaultPublishOn();
            model.addAttribute("colorElements", colorElements != null ? colorElements : new java.util.ArrayList<>());
            model.addAttribute("defaultPublishOn", defaultPublishOn);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("colorElements", new java.util.ArrayList<>());
            model.addAttribute("defaultPublishOn", Constants.FLAG_NO);
        }
        return "setting";
    }
    
    /**
     * URLディレクトリ画面のルーティング
     * コンテンツをタイプ別に階層構造で表示する
     * 
     * @param model モデル
     * @return url-directory.html
     */
    @GetMapping("/url-directory")
    public String urlDirectory(Model model) {
        try {
            // 各タイプのツリーを取得
            List<DirectoryNodeBean> contentTree = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_CONTENT);
            List<DirectoryNodeBean> templateTree = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_TEMPLATE);
            List<DirectoryNodeBean> imageTree = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_IMAGE);
            List<DirectoryNodeBean> fileTree = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_FILE);
            List<DirectoryNodeBean> cssTree = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_STYLESHEET);
            List<DirectoryNodeBean> jsTree = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_SCRIPT);
            List<DirectoryNodeBean> movieTree = content.getUrlDirectoryTree(Constants.CONTENT_TYPE_MOVIE);
            
            model.addAttribute("contentTree", contentTree != null ? contentTree : new ArrayList<>());
            model.addAttribute("templateTree", templateTree != null ? templateTree : new ArrayList<>());
            model.addAttribute("imageTree", imageTree != null ? imageTree : new ArrayList<>());
            model.addAttribute("fileTree", fileTree != null ? fileTree : new ArrayList<>());
            model.addAttribute("cssTree", cssTree != null ? cssTree : new ArrayList<>());
            model.addAttribute("jsTree", jsTree != null ? jsTree : new ArrayList<>());
            model.addAttribute("movieTree", movieTree != null ? movieTree : new ArrayList<>());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("contentTree", new ArrayList<>());
            model.addAttribute("templateTree", new ArrayList<>());
            model.addAttribute("imageTree", new ArrayList<>());
            model.addAttribute("fileTree", new ArrayList<>());
            model.addAttribute("cssTree", new ArrayList<>());
            model.addAttribute("jsTree", new ArrayList<>());
            model.addAttribute("movieTree", new ArrayList<>());
        }
        return "url-directory";
    }
}
