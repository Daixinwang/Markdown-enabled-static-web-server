package org.example.md;

import org.example.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FileTranslator {
    private String WEB_ROOT = "webroot";
    private String BLOG_ROOT = "blog";
    private String HOST = "localhost";
    private String THEME = "article1";
    private MarkdownParser mdParser;
    private YamlParser yamlParser;

    public FileTranslator(Config config) {
        WEB_ROOT = config.getWeb();
        BLOG_ROOT = config.getBlog();
        HOST = config.getHost();
        THEME = config.getTheme();
        mdParser = new MarkdownParser();
        yamlParser = new YamlParser();
    }

    public void translate() {
        List<Article> articles = md2Html();
        genIndexPage(articles);
        genPhotoPage();
    }

    private List<Article> md2Html() {
        File blogDir = new File(BLOG_ROOT);
        File[] files = blogDir.listFiles();
        List<Article> articles = new ArrayList<>();
        assert files != null;
        for (File file: files) {
            if (!file.getName().endsWith(".md")) continue;
            // System.out.println("开始处理文件: " + file.getName());
            Article article = transFile(file);
            // System.out.println("处理完毕，获取文章信息: " + article);
            if (article != null) {
                articles.add(article);
            }
        }
        articles.sort((a1, a2)->{
            if (a1.getPriority() > a2.getPriority()) return 1;
            return a1.getDate().compareTo(a2.getDate());
        });
        System.out.printf("博文处理完毕，共生成%d篇文章\n", articles.size());
        return articles;
    }

    private Article transFile(File file) {
        Article article = new Article();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            reader.readLine();
            for (int i = 0; i < 4; i++) {
                sb.append(reader.readLine()).append("\n");
            }
            reader.readLine();
            article = yamlParser.yaml2Article(sb.toString());
            article.setFileName(file.getName().replace(".md", ".html"));
            sb.setLength(0);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            String html;
            String body = mdParser.md2html(sb.toString());
            if (article.getFileName().equals("about.html")) {
                html = addAboutStyle(body);
            } else {
                html = addPostStyle(body, article.getTitle(), THEME);
            }
            File newFile = new File(WEB_ROOT, article.getFileName());
            boolean ok;
            if (newFile.exists()) {
                ok = newFile.delete();
                if (!ok) throw new Exception("文件删除失败:" + newFile.getAbsolutePath());
            }
            ok = newFile.createNewFile();
            if (!ok) throw new Exception("文件创建失败:" + newFile.getAbsolutePath());
            FileWriter writer = new FileWriter(newFile);
            writer.write(html);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            article = null;
        }
        return article;
    }

    private void genIndexPage(List<Article> articles) {
        try {
            File newFile = new File(WEB_ROOT, "index.html");
            boolean ok;
            if (newFile.exists()) {
                ok = newFile.delete();
                if (!ok) throw new Exception("文件删除失败:" + newFile.getAbsolutePath());
            }
            ok = newFile.createNewFile();
            if (!ok) throw new Exception("文件创建失败:" + newFile.getAbsolutePath());
            FileWriter writer = new FileWriter(newFile);
            StringBuilder sb = new StringBuilder();
            for (Article article : articles) {
                if (article.getFileName().equals("about.html")) {
                    continue;
                }
                sb.append("<li class=\"list-group-item title\">\n");
                sb.append(String.format("<a href='/%s'>%s</a>\n",
                        article.getFileName(), article.getTitle()));
                sb.append(String.format("<div class=\"date\">%s - %s</div> <br/>\n",
                        article.getAuthor(), article.getDate()));
                sb.append("</li>\n");
            }
            String html = addIndexStyle(sb.toString());
            writer.write(html);
            writer.close();
            System.out.println("生成index页面成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void genPhotoPage() {
        try {
            File newFile = new File(WEB_ROOT, "photos.html");
            boolean ok;
            if (newFile.exists()) {
                ok = newFile.delete();
                if (!ok) throw new Exception("文件删除失败:" + newFile.getAbsolutePath());
            }
            ok = newFile.createNewFile();
            if (!ok) throw new Exception("文件创建失败:" + newFile.getAbsolutePath());
            FileWriter writer = new FileWriter(newFile);
            StringBuilder sb = new StringBuilder();

            File picDir = new File(WEB_ROOT + "/pic");
            File[] pics = picDir.listFiles();
            if (pics == null) return;
            int count = 0;
            for (File pic : pics) {
                if (count > 0 && count % 4 == 0) {
                    sb.append("  </div>\n");
                    sb.append("  <div class=\"row\">\n");
                }
                sb.append("    <div class=\"col-xs-6 col-md-3\">\n");
                sb.append(String.format("      <a href=\"/pic/%s\" class=\"thumbnail\">\n", pic.getName()));
                sb.append(String.format("        <img src=\"/pic/%s\" alt=\"%s\" style=\"height: 180px;\">\n", pic.getName(), pic.getName()));
                sb.append("      </a>\n");
                sb.append("  </div>\n");
                count++;
            }
            String html = addPhotoStyle(sb.toString());
            writer.write(html);
            writer.close();
            System.out.printf("生成Photos页面成功, 处理图片%d张\n", count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String addPostStyle(String body, String title, String theme) {
        String head = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + title +"</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css\">\n" +
                "    <link href=\"css\\" + theme + ".css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
                "    <link href=\"css\\" + "main" + ".css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"to-top\" class=\"btn-group\" role=\"group\">\n" +
                "    <button type=\"button\" class=\"btn btn-default btn-lg\" onclick=\"window.scrollTo(0,0);return false;\">\n" +
                "        <span class=\"glyphicon glyphicon-eject\" aria-hidden=\"true\"></span> 顶部\n" +
                "    </button>\n" +
                "    <button type=\"button\" class=\"btn btn-default btn-lg\">\n" +
                "        <a href=\"/index.html\"><span class=\"glyphicon glyphicon-home\" aria-hidden=\"true\"></span> 主页</a>\n" +
                "    </button>\n" +
                "</div>";
        String tail = "\n</body>\n" + "</html>";
        return head + body + tail;
    }

    private String addIndexStyle(String body) {
        String head = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + "MD Blog" +"</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css\">\n" +
                "    <link href=\"css\\" + "index" + ".css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<a class=\"navbar-brand\" href=\"/index.html\" id=\"brand\">MD Blog</a>\n" +
                "<ul class=\"nav nav-tabs\" id=\"page-header\">\n" +
                "    <li role=\"presentation\" class=\"active\"><a href=\"/index.html\">文章</a></li>\n" +
                "    <li role=\"presentation\"><a href=\"/photos.html\">照片</a></li>\n" +
                "    <li role=\"presentation\"><a href=\"/publish.html\">发布</a></li>\n" +
                "    <li role=\"presentation\"><a href=\"/about.html\">关于</a></li>\n" +
                "</ul>\n" +
                "<div class=\"outer\">\n" +
                "    <ul class=\"list-group\">\n";
        String tail = "</ul>\n" +
                "</div>\n\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>\n" +
                "<script src=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js\"></script>" +
                "\n</body>\n" + "</html>";
        return head + body + tail;
    }

    private String addAboutStyle(String body) {
        String head = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + "MD Blog" +"</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css\">\n" +
                "    <link href=\"css\\" + "index" + ".css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<a class=\"navbar-brand\" href=\"/index.html\" id=\"brand\">MD Blog</a>\n" +
                "<ul class=\"nav nav-tabs\" id=\"page-header\">\n" +
                "    <li role=\"presentation\"><a href=\"/index.html\">文章</a></li>\n" +
                "    <li role=\"presentation\"><a href=\"/photos.html\">照片</a></li>\n" +
                "    <li role=\"presentation\"><a href=\"/publish.html\">发布</a></li>\n" +
                "    <li role=\"presentation\" class=\"active\"><a href=\"/about.html\">关于</a></li>\n" +
                "</ul>\n" +
                "<div class=\"outer\">\n";
        String tail = "</div>\n\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>\n" +
                "<script src=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js\"></script>" +
                "\n</body>\n" + "</html>";
        return head + body + tail;
    }

    private String addPhotoStyle(String body) {
        String head = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>" + "MD Blog" +"</title>\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css\">\n" +
                "    <link href=\"css\\" + "index" + ".css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
                "</head>\n" +
                "<body>\n" +
                "<a class=\"navbar-brand\" href=\"/index.html\" id=\"brand\">MD Blog</a>\n" +
                "<ul class=\"nav nav-tabs\" id=\"page-header\">\n" +
                "    <li role=\"presentation\"><a href=\"/index.html\">文章</a></li>\n" +
                "    <li role=\"presentation\" class=\"active\"><a href=\"/photos.html\">照片</a></li>\n" +
                "    <li role=\"presentation\"><a href=\"/publish.html\">发布</a></li>\n" +
                "    <li role=\"presentation\"><a href=\"/about.html\">关于</a></li>\n" +
                "</ul>\n" +
                "<div class=\"outer\">\n" +
                "  <div class=\"row\">\n";
        String tail = "  </div>\n" +
                "</div>\n\n" +
                "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>\n" +
                "<script src=\"https://netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js\"></script>" +
                "\n</body>\n" + "</html>";
        return head + body + tail;
    }
}
