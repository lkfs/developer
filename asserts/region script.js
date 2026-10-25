// ==UserScript==
// @name        region download script
// @namespace   Violentmonkey Scripts
// @match       https://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2023/21*
// @require     http://cdn.bootcss.com/jquery/1.8.3/jquery.min.js
// @grant       GM_addStyle
// @grant       GM_download
// @version     1.0
// @author      Luis
// @description 2024/7/19 08:51:31
// ==/UserScript==
(function () {

    let result = [];
    let itemList = null;

    itemList = $("tr.provincetr td");
    itemList.each(function () {
        let href = $(this).find("a").attr("href");
        let code = href.replace(".html", "")
        let name = $(this).find("a").text();
        result.push({
            "href": href,
            "code": code.substring(0, 2),
            "name": name
        })
    });

    itemList = $("tr.citytr");
    itemList.each(function () {
        let href = $(this).find("a:eq(0)").attr("href");
        let code = $(this).find("a:eq(0)").text();
        let name = $(this).find("a:eq(1)").text();
        result.push({
            "href": href,
            "code": code.substring(0, 4),
            "name": name
        })
    });

    itemList = $("tr.countytr");
    itemList.each(function () {
        let href = $(this).find("a:eq(0)").attr("href");
        let code = $(this).find("a:eq(0)").text();
        let name = $(this).find("a:eq(1)").text();
        result.push({
            "href": href,
            "code": code.substring(0, 6),
            "name": name
        })
    });

    itemList = $("tr.towntr");
    itemList.each(function () {
        let href = $(this).find("a:eq(0)").attr("href");
        let code = $(this).find("a:eq(0)").text();
        let name = $(this).find("a:eq(1)").text();
        result.push({
            "href": href,
            "code": code.substring(0, 9),
            "name": name
        })
    });

    let apiSave = function (item) {
        return new Promise(function (resolve, reject) {
            $.ajax({
                url: "http://127.0.0.1:8880/mongo/admin-mongo-region-save",
                type: "post", contentType: "application/json",
                data: JSON.stringify(item),
                success: function (result, status, xhr) {
                    setTimeout(function () {
                        resolve(result)
                    }, 200)
                },
                error: function (xhr) {
                    reject(xhr.status + " " + xhr.statusText);
                }
            });
        });
    }

    async function loop(result) {
        for (const item of result) {
            let href = window.location.href
            let parentId = href.match(/\d+.html$/gi);
            if (parentId) {
                parentId = parentId[0].replace('.html', '')
            }
            let data = {
                "parentId": parentId,
                "regionId": item.code,
                "regionName": item.name
            }
            console.log("data =", JSON.stringify(data));
            await apiSave(data);
            if (!parentId || parentId.length <= 4) {
                window.open(item.href);
            }
        }
    }

    loop(result)
    //alert(JSON.stringify(result))

    setTimeout(function () {
        window.close();
    }, 10000)

})();
