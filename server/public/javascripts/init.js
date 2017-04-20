if (window.console) {
    console.log("Welcome to your Play application's JavaScript!");
}
(function ($) {
    $(function () {        
        $('.button-collapse').sideNav();

        $(".dropdown-button").dropdown({hover: true, belowOrigin: true});
        // $(".dropdown-button-2").dropdown();

        $(".modal").modal();

    });
})(jQuery);


function Modal() {
}
Modal.confirmDelete = function (modalId) {
    $("#" + modalId).modal("open");
};

function Collapsible() {
}
Collapsible.activate = function () {
    $(".collapsible").collapsible();
};
