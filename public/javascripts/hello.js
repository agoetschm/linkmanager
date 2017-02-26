if (window.console) {
    console.log("Welcome to your Play application's JavaScript!");
}
(function($){
    $(function(){

        $('.button-collapse').sideNav();

        // $('.collapsible').collapsible();

        $(".dropdown-button").dropdown({hover: true, belowOrigin: true});
        // $(".dropdown-button-2").dropdown();


        console.log("test")
    });
})(jQuery);
