define(function(){function e(){if("undefined"==typeof n)throw Error("angularAMD not initialized.  Need to call angularAMD.bootstrap(app) first.")}function r(){var r={};if(t)throw Error("setAlternateAngular can only be called once.");t={},e(),n.extend(t,n),t.module=function(e,o){if("undefined"==typeof o)return r.hasOwnProperty(e)?void 0:n.module(e);var t=n.module.apply(null,arguments),i={name:e,module:t};return a.push(i),r[e]=t,t},window.angular=t}function o(){}var n,t,i,l,a=[],u={};return o.prototype.route=function(e){var r;if(e.hasOwnProperty("controllerUrl")?(r=e.controllerUrl,delete e.controllerUrl):"string"==typeof e.controller&&(r=e.controller),r){var o=e.resolve||{};o.__load=["$q","$rootScope",function(e,o){var n=e.defer();return require([r],function(){n.resolve(),o.$apply()}),n.promise}],e.resolve=o}return e},o.prototype.appname=function(){return e(),i},o.prototype.processQueue=function(){if(e(),"undefined"==typeof t)throw Error("Alternate angular not set.  Make sure that `enable_ngload` option has been set when calling angularAMD.bootstrap");for(;a.length;){var r,o=a.shift(),i=o.module._invokeQueue;for(r=0;r<i.length;r+=1){var c=i[r],d=c[0],f=c[1],p=c[2];if(u.hasOwnProperty(d)){var s=u[d];s[f].apply(null,p)}else console.error("'"+d+"' not found!!!")}o.module._runBlocks&&angular.forEach(o.module._runBlocks,function(e){l.invoke(e)}),n.module(o.name,[],n.noop)}},o.prototype.getCachedProvider=function(r){return e(),"__orig_angular"===r?n:"__alt_angular"===r?t:u[r]},o.prototype.inject=function(){return e(),l.invoke.apply(null,arguments)},o.prototype.reset=function(){"undefined"!=typeof n&&(window.angular=n,t=void 0,a=[],i=void 0,l=void 0,u={},n=void 0)},o.prototype.bootstrap=function(e,o,t){if("undefined"!=typeof n)throw Error("bootstrap can only be called once.");n=angular,"undefined"==typeof o&&(o=!0),t=t||document,e.config(["$controllerProvider","$compileProvider","$filterProvider","$animateProvider","$provide",function(r,o,n,t,i){u={$controllerProvider:r,$compileProvider:o,$filterProvider:n,$animateProvider:t,$provide:i},e.register={controller:r.register,directive:o.directive,filter:n.register,factory:i.factory,service:i.service,constant:i.constant,value:i.value,animation:t.register}}]),e.run(["$injector",function(e){l=e,u.$injector=l}]),i=e.name,n.element(document).ready(function(){n.bootstrap(t,[i])}),o&&r()},new o});