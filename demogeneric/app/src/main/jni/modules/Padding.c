#include "../thnets.h"
#include <stdio.h>
#include <string.h>

int nnload_Padding(struct module *mod, struct nnmodule *n)
{
	struct table *t = n->table;
	mod->type = MT_Padding;
	mod->updateOutput = nn_Padding_updateOutput;
	struct Padding *m = &mod->Padding;
	m->dim = TableGetNumber(t, "dim");
	m->pad = TableGetNumber(t, "pad");
	m->nInputDim = TableGetNumber(t, "nInputDim");
	m->index = TableGetNumber(t, "index");
	m->value = TableGetNumber(t, "value");
	if(m->value || m->index != 1 || m->pad < 0 || m->dim != 3 || m->nInputDim != 3)
		printf("Unsupported values for the padding layer");
	return 0;
}

THFloatTensor *nn_Padding_updateOutput(struct module *module, THFloatTensor *input)
{
	THFloatTensor_resize3d(module->output, input->size[0], input->size[1], input->size[2]);
	THFloatTensor_copy(module->output, input);
	float *paddata = THFloatTensor_data(module->output);
	memset(paddata + THFloatTensor_nElement(input), 0,
		(THFloatTensor_nElement(module->output) - THFloatTensor_nElement(input)) * sizeof(float));
	return module->output;
}
