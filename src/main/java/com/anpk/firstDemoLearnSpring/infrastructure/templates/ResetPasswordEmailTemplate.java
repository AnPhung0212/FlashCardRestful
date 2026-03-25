package com.anpk.firstDemoLearnSpring.infrastructure.templates;

public final class ResetPasswordEmailTemplate {
    private static final String TEMPLATE = """
<!DOCTYPE html>
<html lang=\"vi\">
<head>
<meta charset=\"UTF-8\">
<title>Đặt lại mật khẩu</title>
<style>
body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f7f7f7; }
.email-container { max-width: 600px; margin: 0 auto; background-color: #fff; }
.email-header { background: linear-gradient(135deg, #4b6cb7 0%, #182848 100%); padding: 30px 20px; text-align: center; border-radius: 5px 5px 0 0; }
.email-header h1 { color: white; margin: 0; font-size: 24px; font-weight: 600; }
.email-body { padding: 30px; }
.button { display: inline-block; padding: 12px 24px; background-color: #4b6cb7; color: white; text-decoration: none; border-radius: 4px; font-weight: 600; margin: 20px 0; }
.email-footer { background-color: #f1f1f1; padding: 20px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 5px 5px; }
</style>
</head>
<body>
<div class=\"email-container\">
    <div class=\"email-header\">
        <h1>Đặt lại mật khẩu</h1>
    </div>
    <div class=\"email-body\">
        <p>Xin chào {{name}},</p>
        <p>Bạn vừa yêu cầu đặt lại mật khẩu. Nhấn vào nút bên dưới để đặt lại (có hiệu lực 15 phút):</p>
        <div style=\"text-align: center;\">
            <a href=\"{{resetUrl}}\" class=\"button\">Đặt lại mật khẩu</a>
        </div>
        <p>Nếu nút trên không hoạt động, hãy sao chép và dán đường liên kết sau vào trình duyệt:</p>
        <p style=\"word-break: break-all; color: #4b6cb7; background-color: #f9f9f9; padding: 10px; border-radius: 4px;\">
            <a href=\"{{resetUrl}}\">{{resetUrl}}</a>
        </p>
        <p>Bạn nhận được email này vì đã yêu cầu đặt lại mật khẩu.</p>
    </div>
    <div class=\"email-footer\">
        <p>Đây là email tự động, vui lòng không trả lời.</p>
    </div>
</div>
</body>
</html>
""";

    private ResetPasswordEmailTemplate() {}

    public static String render(String name, String resetUrl) {
        return TEMPLATE
                .replace("{{name}}", name == null ? "" : name)
                .replace("{{resetUrl}}", resetUrl);
    }
}
