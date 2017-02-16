$(function() {

    Morris.Bar({
        element: 'morris-area-chart',
        data: [{
            day: '2016-01-01',
            ods: 2666,
            fact: null,
            dim: 2647,
            dwa:1123,
            dwd:2312,
            app:1231
        },{
            day: '2016-01-02',
            ods: 3666,
            fact: null,
            dim: 3647,
            dwa:1223,
            dwd:2312,
            app:2231
        }],
        xkey: 'day',
        ykeys: ['ods', 'fact', 'dim','dwa','dwd','app'],
        labels: ['ods', 'fact', 'dim', 'dwa', 'dwd','app'],
        pointSize: 2,
        parseTime:false,
        hideHover: 'auto',
        resize: true
    });
});
