import "@testing-library/jest-dom";

// Polyfill para pointer capture exigido por Radix Select em jsdom
if (typeof Element !== "undefined") {
  // @ts-ignore
  Element.prototype.hasPointerCapture =
    Element.prototype.hasPointerCapture || (() => false);
  // @ts-ignore
  Element.prototype.setPointerCapture =
    Element.prototype.setPointerCapture || (() => {});
  // @ts-ignore
  Element.prototype.releasePointerCapture =
    Element.prototype.releasePointerCapture || (() => {});
  // jsdom não implementa scrollIntoView
  // @ts-ignore
  Element.prototype.scrollIntoView =
    Element.prototype.scrollIntoView || (() => {});
}
