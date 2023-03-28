$(document).ready(function () {
    $('a.btn').click(function (event) {
      event.preventDefault();
      $('#overlayWindow').fadeIn(297, function () {
          $('#popupDownloadWindow').css('display', 'block').animate({opacity: 1}, 198);
      });
    });

    $('#popupDownloadWindow__close, #overlayWindow').click(function () {
       $('#popupDownloadWindow').animate({opacity: 1}, 198, function () {
           $(this).css('display', 'none');
           $('#overlayWindow').fadeOut(297);
       });
    });
})