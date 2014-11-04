/**
 * 组件提供字符串到JSON对象之间的互转
 *
 * @homepage    http://www.baidufe.com/component/json-tool/index.html
 * @author      zhaoxianlie
 */
var JsonTool = (function(){
    /**
     * 组件版本
     * @type {String}
     */
    var version = "1.0";

    /**
     * 字符串转义
     * @type {Object}
     */
    var escapeMap = {
        "\b": '\\b',
        "\t": '\\t',
        "\n": '\\n',
        "\f": '\\f',
        "\r": '\\r',
        '"' : '\\"',
        "\\": '\\\\'
    };

    /**
     * 字符串编码
     * @param source
     * @return {String}
     */
    function encodeString(source) {
        if (/["\\\x00-\x1f]/.test(source)) {
            source = source.replace( /["\\\x00-\x1f]/g, function (match) {
                var c = escapeMap[match];
                if (c) return c;
                c = match.charCodeAt();
                return "\\u00" + Math.floor(c / 16).toString(16) + (c % 16).toString(16);
            });
        }
        return '"' + source + '"';
    }

    /**
     * 数组编码
     * @param source
     * @return {String}
     */
    function encodeArray(source) {
        var result = ["["],
            l = source.length,
            preComma, i, item;

        for (i = 0; i < l; i++) {
            item = source[i];

            switch (typeof item) {
                case "undefined":
                case "function":
                case "unknown":
                    break;
                default:
                    if(preComma) {
                        result.push(',');
                    }
                    result.push(stringify(item));
                    preComma = 1;
            }
        }
        result.push("]");
        return result.join("");
    }

    /**
     * 日期格式数据编码
     * @param source
     * @return {String}
     */
    function encodeDate(source){
        function _pad(s) {
            return s < 10 ? '0' + s : s;
        }
        return '"' + source.getFullYear() + "-"
            + _pad(source.getMonth() + 1) + "-"
            + _pad(source.getDate()) + "T"
            + _pad(source.getHours()) + ":"
            + _pad(source.getMinutes()) + ":"
            + _pad(source.getSeconds()) + '"';
    }

    /**
     * 编码简单JSON格式数据，如：{key:value}，如果value为复杂类型，继续stringify，递归
     * @param value
     * @return {String}
     */
    function encodeJSON(value) {
        var result = ['{'],
            preComma,
            item;

        if(value instanceof HTMLElement || value instanceof HTMLDocument) {
            return '"' + value.toString() + '"';
        }
        for (var key in value) {
            if (Object.prototype.hasOwnProperty.call(value, key)) {
                item = value[key];
                if (preComma) {
                    result.push(',');
                }
                preComma = 1;
                switch (typeof item) {
                    case 'undefined':
                    case 'unknown':
                        result.push(stringify(key) + ':' + String(item));
                        break;
                    case 'function':
                        result.push(stringify(key) + ':' + item.toString());
                        break;
                    default:
                        result.push(stringify(key) + ':' + stringify(item));
                }
            }
        }
        result.push('}');
        return result.join('');
    }

    /**
     * 将JSON对象转换为字符串；其中JSON只能是由基本数据类型组成的，不能是HTMLElement对象
     *
     * @param   value    由undefined、null、Number、Boolean、String、Date基本类型组成的
     *                   任意JSON对象，也可以是由多个JSON组成的Array
     * @return  {String} 转换后的字符串值
     */
    function stringify(value) {
        switch (typeof value) {
            case 'undefined':
                return 'undefined';

            case 'number':
                return isFinite(value) ? String(value) : "null";

            case 'string':
                return encodeString(value);

            case 'boolean':
                return String(value);

            default:
                if (value === null) {
                    return 'null';
                } else if (value instanceof Array) {
                    return encodeArray(value);
                } else if (value instanceof Date) {
                    return encodeDate(value);
                } else {
                    return encodeJSON(value);
                }
        }
    }

    /**
     * 将字符串转换为JSON对象
     *
     * @param {String}  strJson    需要转换为JSON对象的字符串；字符串也必须要求是JSON数据格式的
     * @return {Object}            字符串格式合法时，返回对应的JSON对象；否则，返回null
     */
    function parse(strJson) {
        var value = null;
        try{
            value = (new Function("return (" + strJson + ")"))();
        }catch(e){
            value = null;
        }
        return value;
    }

    return {
        version : version,
        stringify : stringify,
        parse : parse
    };
})();
