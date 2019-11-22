var gulp = require('gulp');
var insertString = require('gulp-insert-string-into-tag');

gulp.task('default', function () {
    return gulp.src('.vuepress/dist/index.html')
        .pipe(insertString.append({
            startTag:'<body>',
            endTag:'</body>',
            string:`<style type="text/css">
#cnzz_stat_icon_1278228116{
    display: block;
    margin: 0 auto;
}
</style>
<script type="text/javascript">var cnzz_protocol = (("https:" == document.location.protocol) ? "https://" : "http://");document.write(unescape("%3Cspan id='cnzz_stat_icon_1278228116'%3E%3C/span%3E%3Cscript src='" + cnzz_protocol + "s9.cnzz.com/z_stat.php%3Fid%3D1278228116%26show%3Dpic' type='text/javascript'%3E%3C/script%3E"));</script>`
        }))
        .pipe(gulp.dest('.vuepress/dist'));
});