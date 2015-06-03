const postcss = require('gulp-postcss');
const gulp = require('gulp');
const cssnext = require('cssnext');
const csswring = require('csswring');

gulp.task('css', function () {
  return gulp.src("src/css/style.css")
    .pipe(postcss([
      cssnext(),
      csswring
    ]))
    .pipe(gulp.dest("resources/public/css"));
});

gulp.task("watch", function () {
  gulp.start("css");
  gulp.watch("src/css/**/*.css", ["css"]);
});

gulp.task("build", ["css"]);
gulp.task("default", ["watch"]);
