package org.rl337.math.lang;

import java.util.Map;

public class MapVariableResolver implements VariableResolver {
    private Map<String, Token> mMap;
    
    public MapVariableResolver(Map<String, Token> map) {
        mMap = map;
    }

    public Token resolve(String name, int pos) throws ExpressionEvaluationException {
        if (name == null) {
            throw new ExpressionEvaluationException("Could not resolve a null variable");
        }
        
        String toLower = name.toLowerCase();
        
        if (mMap == null || !mMap.containsKey(toLower)) {
            throw new ExpressionEvaluationException("Could not resolve identifier '" + toLower + "'");
        }
        
        Token mapValue = mMap.get(toLower);
        if (mapValue == null) {
            return null;
        }
        
        return new Token(mapValue, pos);
    }
    
}
