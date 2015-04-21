package com.craigburke.document.builder.render

import com.craigburke.document.builder.PdfDocument
import com.craigburke.document.core.Cell
import com.craigburke.document.core.Table
import com.craigburke.document.core.TextBlock

/**
 * Rendering element for the Column node
 * @author Craig Burke
 */
class CellElement implements Renderable {
    Cell column
    List<Renderable> childElements = []

    CellElement(Cell column, PdfDocument pdfDocument, float startX) {
        this.column = column
        this.startX = startX
        this.pdfDocument = pdfDocument

        Table table = column.parent.parent
        int renderWidth = column.width - (table.padding * 2)
        float childStartX = startX + table.padding
        column.children.each { child ->
            if (child instanceof TextBlock) {
                childElements << new ParagraphElement(child, pdfDocument, childStartX, renderWidth)
            }
            else if (child instanceof Table) {
                childElements << new TableElement(child, pdfDocument, childStartX)
            }
        }
    }

    private float getPadding() {
        column.parent.parent.padding
    }

    boolean getFullyParsed() {
        childElements.every { it.fullyParsed }
    }

    float getTotalHeight() {
        (childElements*.totalHeight.sum() ?: 0f) as float
    }

    float getParsedHeight() {
        float parsedHeight = (childElements*.parsedHeight.sum() ?: 0f) as float
        if (onFirstPage) {
            parsedHeight += padding
        }
        if (fullyParsed) {
            parsedHeight += padding
        }
        parsedHeight
    }

    void renderElement(float startY) {
        if (!parsedHeight) {
            return
        }

        float childY = startY
        if (onFirstPage) {
            childY += padding
        }
        childElements*.render(childY)
    }

    void parse(float height) {
        childElements*.parse(height)
    }

    boolean isOnLastRow() {
        column.rowspan == column.currentRow
    }

}

