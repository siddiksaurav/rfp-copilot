import {clsx} from "clsx";
import {twMerge} from "tailwind-merge";

export function cn(...inputs) {
  return twMerge(clsx(inputs));
}

export const parseRequirementResponse = (responseText) => {
  try {
    // Check if it's the AssistantMessage format
    if (responseText.includes('AssistantMessage [')) {
      // Extract textContent using regex
      const textContentMatch = responseText.match(/textContent=([^,]+(?:,[^,]*)*?)(?:,\s*metadata=|$)/s);
      if (textContentMatch) {
        let textContent = textContentMatch[1];

        // Remove leading/trailing quotes if present
        if (textContent.startsWith('"') && textContent.endsWith('"')) {
          textContent = textContent.slice(1, -1);
        }

        // Handle escaped characters
        textContent = textContent
            .replace(/\\n/g, '\n')
            .replace(/\\t/g, '\t')
            .replace(/\\"/g, '"')
            .replace(/\\\\/g, '\\');

        return {
          success: true,
          content: textContent,
          messageType: 'ASSISTANT',
          rawResponse: responseText
        };
      }
    }

    // Try to parse as JSON
    const parsed = JSON.parse(responseText);
    if (parsed.textContent) {
      return {
        success: true,
        content: parsed.textContent,
        messageType: parsed.messageType || 'ASSISTANT',
        rawResponse: responseText
      };
    }

    return {
      success: true,
      content: responseText,
      messageType: 'PLAIN_TEXT',
      rawResponse: responseText
    };
  } catch {
    // If not JSON, treat as plain text
    return {
      success: true,
      content: responseText,
      messageType: 'PLAIN_TEXT',
      rawResponse: responseText
    };
  }
};