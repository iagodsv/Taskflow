import { cn } from "@/lib/utils";
import * as React from "react";

type AsTag = "span" | "p" | "small" | "label" | "div";

export interface TextProps extends React.HTMLAttributes<HTMLElement> {
  as?: AsTag;
  size?: "xs" | "sm" | "md" | "lg" | "xl" | "2xl";
  weight?: "normal" | "medium" | "semibold" | "bold";
  muted?: boolean;
}

const sizeClass: Record<NonNullable<TextProps["size"]>, string> = {
  xs: "text-xs",
  sm: "text-sm",
  md: "text-base",
  lg: "text-lg",
  xl: "text-xl",
  "2xl": "text-2xl",
};

const weightClass: Record<NonNullable<TextProps["weight"]>, string> = {
  normal: "font-normal",
  medium: "font-medium",
  semibold: "font-semibold",
  bold: "font-bold",
};

export function Text({
  as = "span",
  size = "md",
  weight = "normal",
  muted,
  className,
  children,
  ...rest
}: TextProps) {
  const Tag = as as unknown as React.ElementType;
  return (
    <Tag
      className={cn(
        sizeClass[size],
        weightClass[weight],
        muted ? "text-muted-foreground" : "text-foreground",
        className,
      )}
      {...rest}
    >
      {children}
    </Tag>
  );
}
