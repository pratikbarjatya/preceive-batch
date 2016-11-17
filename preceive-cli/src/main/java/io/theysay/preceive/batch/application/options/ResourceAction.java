package io.theysay.preceive.batch.application.options;

import io.theysay.preceive.batch.api.ApiResource;
import io.theysay.preceive.batch.api.PreCeiveClient;
import io.theysay.preceive.batch.cmdline.Action;
import io.theysay.preceive.batch.cmdline.ShellContext;
import io.theysay.preceive.batch.options.ClientOptions;
import io.theysay.preceive.batch.sources.Sources;
import io.theysay.preceive.batch.utils.Datum;

import java.io.IOException;

public abstract class ResourceAction extends Action {
    protected ApiResource resource;

    public ResourceAction(ApiResource resource, String key, String usage) {
        super(key, usage);
        this.resource = resource;
    }

    public void add(ShellContext context) throws IOException {
        PreCeiveClient client = ClientOptions.getClient();
        java.util.List<Datum> items = Sources.read(context.nextArgument("XLSX File"), Datum.class);
        for (Datum datum : items) {
            try {
                client.add(resource, datum);
            } catch (Exception e) {
                context.println("Unable to create " + datum);
            }
        }

    }


    public static class List extends ResourceAction {
        public List(ApiResource resource, String key, String usage) {
            super(resource, key, usage);
        }

        @Override
        public void execute(ShellContext context) throws Exception {
            context.println("List of " + resource.label);
            context.println(resource.tsvHeader());
            for (Datum item : ClientOptions.getClient().list(resource)) {
                context.println(item.tsv(resource.fields));
            }
        }


    }

    public static class Clear extends ResourceAction {
        public Clear(ApiResource resource, String key, String usage) {
            super(resource, key, usage);
        }

        @Override
        public void execute(ShellContext context) throws Exception {
            ClientOptions.getClient().deleteAll(resource);
        }
    }

    public static class Set extends ResourceAction {
        public Set(ApiResource resource, String key, String usage) {
            super(resource, key, usage);
        }

        @Override
        public void execute(ShellContext context) throws Exception {
            PreCeiveClient client = ClientOptions.getClient();
            client.deleteAll(resource);
            add(context);
        }
    }

    public static class Add extends ResourceAction {
        public Add(ApiResource resource, String key, String usage) {
            super(resource, key, usage);
        }

        @Override
        public void execute(ShellContext context) throws Exception {
            add(context);
        }
    }
}
