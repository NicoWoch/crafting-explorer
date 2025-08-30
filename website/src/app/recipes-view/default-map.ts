export class DefaultMap<K, V> extends Map<K, V> {
    constructor(private defaultFactory: () => V, entries?: readonly (readonly [K, V])[] | null) {
        super(entries);
    }

    override get(key: K): V {
        if (!this.has(key)) {
            const value = this.defaultFactory();
            this.set(key, value);
            return value;
        }
        return super.get(key)!;
    }
}