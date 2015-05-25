const autoprefixer = require('gulp-autoprefixer');
const gulp = require('gulp');
const minifyCSS = require('gulp-minify-css');
const plumber = require('gulp-plumber');
const sass = require('gulp-sass');

gulp.task("sass", function () {
  gulp.src("src/sass/style.scss")
    .pipe(plumber())
    .pipe(sass())
    .pipe(autoprefixer({
      browsers: ["last 3 versions"],
      cascade: false
    }))
    .pipe(minifyCSS())
    .pipe(gulp.dest("resources/public/css"));
});

gulp.task("watch", function () {
  gulp.start("sass");
  gulp.watch("src/sass/**/*.scss", ["sass"]);
});

gulp.task("build", ["sass"]);
gulp.task("default", ["watch"]);
